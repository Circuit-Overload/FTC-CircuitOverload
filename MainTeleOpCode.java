package org.firstinspires.ftc.teamcode;

import static java.lang.Math.abs;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

// Import the Limelight FTC library classes
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;

@TeleOp(name="TeleOp_Main", group="Linear OpMode")
public class Proto1code extends LinearOpMode {

    // Hardware Declarations
    private DcMotor motor_front_left, motor_front_right, motor_back_left, motor_back_right;
    private DcMotor motor_intake;
    private DcMotorEx motor_shooter;
    private Servo servo_intake;

    // Shooter closed-loop velocity control (holds RPM despite friction / battery sag)
    // goBILDA Yellow Jacket encoder: 28 ticks per motor revolution
    private static final double SHOOTER_TARGET_RPM = 2700.0;
    private static final double SHOOTER_TICKS_PER_REV = 28.0;
    private static final double SHOOTER_TARGET_TPS = SHOOTER_TARGET_RPM * SHOOTER_TICKS_PER_REV / 60.0; // = 1540
    // Shooter spins in the negative direction (same as the old setPower(-0.55))
    private static final double SHOOTER_DIRECTION = -1.0;

    // Shooter velocity PIDF gains (tune on the robot, change ONE at a time)
    // F = 32767 / max free-spin ticks per second (about 13 for ~2520 TPS); P = push strength;
    // I = removes small steady error; D = damps overshoot (keep 0 unless P oscillates)
    private static final double SHOOTER_P = 30.0;
    private static final double SHOOTER_I = 0.0;
    private static final double SHOOTER_D = 0.5;
    private static final double SHOOTER_F = 14.5;
    /** RPM within this band of target counts as "at speed" for debug telemetry */
    private static final double SHOOTER_AT_SPEED_TOLERANCE_RPM = 150.0;

    // Limelight Declaration
    private Limelight3A limelight;

    // Toggle Tracking Variables (Must remain fields to persist across loop iterations)
    private boolean intakeOn = false;
    private boolean lastAState = false;

    private boolean shooterOn = false;
    private boolean lastXState = false;

    // Servo Angle Scaling for a 300-degree servo (Valid range: 0.0 to 1.0)
    // Starts higher (~100 degrees) and moves down toward 90 degrees when button B is pressed
    private static final double SERVO_START_POS = 50.0 / 300.0;
    private static final double SERVO_DOWN_POS  = 05.0 / 300.0;

    @Override
    public void runOpMode() {

        // Hardware Mapping
        motor_front_left = hardwareMap.get(DcMotor.class, "motor_front_left");
        motor_back_left = hardwareMap.get(DcMotor.class, "motor_back_left");
        motor_front_right = hardwareMap.get(DcMotor.class, "motor_front_right");
        motor_back_right = hardwareMap.get(DcMotor.class, "motor_back_right");

        motor_intake = hardwareMap.get(DcMotor.class, "motor_intake");
        motor_shooter = hardwareMap.get(DcMotorEx.class, "motor_shooter");
        // Use the built-in encoder so the hub regulates velocity (PIDF) instead of raw voltage
        motor_shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor_shooter.setVelocityPIDFCoefficients(SHOOTER_P, SHOOTER_I, SHOOTER_D, SHOOTER_F);
        servo_intake = hardwareMap.get(Servo.class, "servo_intake");

        // Map Limelight (ensure it matches the name configured in your Robot Controller config)
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        // Reverse left side motors so positive power drives forward
        motor_front_left.setDirection(DcMotor.Direction.REVERSE);
        motor_back_left.setDirection(DcMotor.Direction.REVERSE);
        motor_front_right.setDirection(DcMotor.Direction.FORWARD);
        motor_back_right.setDirection(DcMotor.Direction.FORWARD);

        // Initialize and start the Limelight (Pipeline 0 is your color tracking pipeline)
        limelight.setPollRateHz(100);
        limelight.start();
        limelight.pipelineSwitch(0);

        telemetry.addData("Status", "Initialized. Waiting for Start...");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // -------------------------------------------------------------
            // 1. LIMELIGHT DATA POLLING
            // -------------------------------------------------------------
            LLResult result = limelight.getLatestResult();
            boolean targetValid = false;
            double tx = 0.0; // Horizontal offset from crosshair (-29.8 to 29.8 degrees)

            if (result != null && result.isValid()) {
                targetValid = true;
                tx = result.getTx();
            }

            // -------------------------------------------------------------
            // 2. STRAFE / DRIVE LOGIC WITH OPTIONAL BLOB AUTO-AIM (Hold Left Bumper)
            // -------------------------------------------------------------
            double left_y = -gamepad1.left_stick_y;
            double left_x = gamepad1.left_stick_x;
            double right_x = gamepad1.right_stick_x;

            // Add deadzones
            if (abs(left_y) <= 0.05) left_y = 0;
            if (abs(left_x) <= 0.05) left_x = 0;
            if (abs(right_x) <= 0.05) right_x = 0;

            // Precision drive on gamepad2: lower sensitivity, overrides gamepad1 drive while its sticks are moved
            final double PRECISION_SCALE = 0.3;
            double g2_y = -gamepad2.left_stick_y;
            double g2_x = gamepad2.left_stick_x;
            double g2_r = gamepad2.right_stick_x;
            if (abs(g2_y) <= 0.05) g2_y = 0;
            if (abs(g2_x) <= 0.05) g2_x = 0;
            if (abs(g2_r) <= 0.05) g2_r = 0;
            boolean gamepad1Moving = left_y != 0 || left_x != 0 || right_x != 0;
            if (!gamepad1Moving && (g2_y != 0 || g2_x != 0 || g2_r != 0)) {
                left_y = g2_y * PRECISION_SCALE;
                left_x = g2_x * PRECISION_SCALE;
                right_x = g2_r * PRECISION_SCALE;
            }

            // Auto-Aim Assist: If Left Bumper is held and a blob is visible, override right_x to track the blob!
            if (gamepad1.left_bumper && targetValid) {
                double steerKp = 0.03; // Tweak this gain value based on how fast it turns
                right_x = tx * steerKp;
            }

            // Calculate Mecanum powers
            double flPower = left_y + left_x + right_x;
            double blPower = left_y - left_x + right_x;
            double frPower = left_y - left_x - right_x;
            double brPower = left_y + left_x - right_x;

            // Normalize powers if stick magnitude exceeds 1.0
            double max = Math.max(-1.0,
                    Math.max(abs(flPower), Math.max(abs(blPower),
                            Math.max(abs(frPower), abs(brPower)))));
            if (max > 1.0) {
                flPower /= max;
                blPower /= max;
                frPower /= max;
                brPower /= max;
            }

            double acceleration = 0.75 + (gamepad1.right_trigger * 0.25);

            motor_front_left.setPower(flPower * acceleration);
            motor_back_left.setPower(blPower * acceleration);
            motor_front_right.setPower(frPower * acceleration);
            motor_back_right.setPower(brPower * acceleration);

            // -------------------------------------------------------------
            // 3. INTAKE & SHOOTER MOTOR TOGGLES (Buttons A & X)
            // -------------------------------------------------------------
            boolean currentAState = gamepad1.a;
            if (currentAState && !lastAState) {
                intakeOn = !intakeOn;
            }
            lastAState = currentAState;

            boolean currentXState = gamepad1.x;
            if (currentXState && !lastXState) {
                shooterOn = !shooterOn;
            }
            lastXState = currentXState;

            motor_intake.setPower(intakeOn ? 0.667 : 0.0);
            // Closed-loop: hold target RPM (SHOOTER_TARGET_RPM); hub compensates for load / battery sag
            motor_shooter.setVelocity(shooterOn ? SHOOTER_DIRECTION * SHOOTER_TARGET_TPS : 0.0);

            // Fixed power fallback (open loop): -55% when on
            // motor_shooter.setPower(shooterOn ? -0.55 : 0.0);

            // -------------------------------------------------------------
            // 4. SERVO POSITION CONTROL (Button B)
            // -------------------------------------------------------------
            if (gamepad1.b) {
                servo_intake.setPosition(SERVO_DOWN_POS);
            } else {
                servo_intake.setPosition(SERVO_START_POS);
            }

            // -------------------------------------------------------------
            // 5. TELEMETRY DISPLAY (Debugging info for the Limelight)
            // -------------------------------------------------------------
            double shooterVelocityTps = motor_shooter.getVelocity();
            double shooterActualRpm = Math.abs(shooterVelocityTps) * 60.0 / SHOOTER_TICKS_PER_REV;
            boolean shooterAtSpeed = shooterOn
                    && Math.abs(shooterActualRpm - SHOOTER_TARGET_RPM) <= SHOOTER_AT_SPEED_TOLERANCE_RPM;

            telemetry.addData("Shooter Target RPM", shooterOn ? SHOOTER_TARGET_RPM : 0.0);
            telemetry.addData("Shooter Actual RPM", shooterActualRpm);
            telemetry.addData("Shooter ON (X toggle)", shooterOn);
            telemetry.addData("Shooter at speed?", shooterAtSpeed);
            telemetry.addData("Shooter encoder position", motor_shooter.getCurrentPosition());
            telemetry.addData("Shooter velocity TPS (raw)", shooterVelocityTps);
            telemetry.addData("Shooter commanded TPS", shooterOn ? SHOOTER_DIRECTION * SHOOTER_TARGET_TPS : 0.0);
            if (shooterOn && shooterActualRpm < 50.0) {
                telemetry.addLine("Shooter: encoder may be unplugged if position/RPM stay ~0");
            }

            telemetry.addData("Limelight Target Found", targetValid);
            if (targetValid) {
                telemetry.addData("Horizontal Offset (tx)", tx);
                telemetry.addData("Vertical Offset (ty)", result.getTy());
                telemetry.addData("Target Area (ta)", result.getTa());
            }
            telemetry.update();
        }
    }
}
