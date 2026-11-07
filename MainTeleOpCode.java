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
public class MainTeleOpCode extends LinearOpMode {

    // Hardware Declarations
    private DcMotor motor_front_left, motor_front_right, motor_back_left, motor_back_right;
    private DcMotor motor_intake;
    private DcMotorEx motor_shooter;
    private Servo servo_intake;

    // Shooter closed-loop velocity control (holds RPM despite friction / battery sag)
    // goBILDA Yellow Jacket encoder: 28 ticks per motor revolution
    private static final double SHOOTER_TARGET_RPM = 2940.0;
    private static final double SHOOTER_TICKS_PER_REV = 28.0;
    private static final double SHOOTER_TARGET_TPS = SHOOTER_TARGET_RPM * SHOOTER_TICKS_PER_REV / 60.0; // = 1540
    // Shooter spins in the negative direction (same as the old setPower(-0.55))
    private static final double SHOOTER_DIRECTION = 1.0;

    // Shooter velocity PIDF gains (tune on the robot, change ONE at a time)
    // F = 32767 / max free-spin ticks per second (about 13 for ~2520 TPS); P = push strength;
    // I = removes small steady error; D = damps overshoot (keep 0 unless P oscillates)
    private static final double SHOOTER_P = 29.0;
    private static final double SHOOTER_I = 0.0;
    private static final double SHOOTER_D = 2.5;  //1.5
    private static final double SHOOTER_F = 12.5;   //13
    /** RPM within this band of target counts as "at speed" for debug telemetry */
    private static final double SHOOTER_AT_SPEED_TOLERANCE_RPM = 150.0;

    // Limelight Declaration
    private Limelight3A limelight;

    // Toggle Tracking Variables (Must remain fields to persist across loop iterations)
    private boolean intakeOn = false;
    private boolean lastIntakeButtonState = false;

    private boolean shooterOn = false;
    private boolean lastShooterButtonState = false;

    // Speed toggle: press B on gamepad1 to switch between full and 65% speed
    private boolean slowMode = false;
    private boolean lastM1State = false;

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

            // Speed toggle: B button on gamepad1 switches between 100% and 65% speed
            boolean currentM1State = gamepad1.b;
            if (currentM1State && !lastM1State) {
                slowMode = !slowMode;
            }
            lastM1State = currentM1State;

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
            double speedMultiplier = slowMode ? 0.65 : 1.0;

            motor_front_left.setPower(flPower * acceleration * speedMultiplier);
            motor_back_left.setPower(blPower * acceleration * speedMultiplier);
            motor_front_right.setPower(frPower * acceleration * speedMultiplier);
            motor_back_right.setPower(brPower * acceleration * speedMultiplier);

            // -------------------------------------------------------------
            // 3. INTAKE & SHOOTER MOTOR TOGGLES (Left Bumper & Right Bumper)
            // -------------------------------------------------------------
            boolean currentIntakeButtonState = gamepad2.left_bumper;
            if (currentIntakeButtonState && !lastIntakeButtonState) {
                intakeOn = !intakeOn;
            }
            lastIntakeButtonState = currentIntakeButtonState;

            boolean currentShooterButtonState = gamepad2.right_bumper;
            if (currentShooterButtonState && !lastShooterButtonState) {
                shooterOn = !shooterOn;
            }
            lastShooterButtonState = currentShooterButtonState;

            motor_intake.setPower(intakeOn ? 0.667 : 0.0);
            // Closed-loop: hold target RPM (SHOOTER_TARGET_RPM); hub compensates for load / battery sag
            motor_shooter.setVelocity(shooterOn ? SHOOTER_DIRECTION * SHOOTER_TARGET_TPS : 0.0);

            // Fixed power fallback (open loop): -55% when on
            // motor_shooter.setPower(shooterOn ? -0.55 : 0.0);

            // -------------------------------------------------------------
            // 4. SERVO POSITION CONTROL (Hold Left Trigger or Right Trigger)
            // -------------------------------------------------------------
            boolean servoDownHeld = gamepad2.left_trigger > 0.5 || gamepad2.right_trigger > 0.5;
            if (servoDownHeld) {
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

            if (slowMode) {
                telemetry.addLine("*** SLOW MODE ON (65%) ***");
                telemetry.addData("Speed multiplier", speedMultiplier);
                telemetry.addData("Acceleration", acceleration);
                telemetry.addData("Left stick (x, y)", "%.2f, %.2f", gamepad1.left_stick_x, gamepad1.left_stick_y);
                telemetry.addData("Right stick x", "%.2f", gamepad1.right_stick_x);
                telemetry.addData("Motor power FL/FR", "%.2f / %.2f",
                        motor_front_left.getPower(), motor_front_right.getPower());
                telemetry.addData("Motor power BL/BR", "%.2f / %.2f",
                        motor_back_left.getPower(), motor_back_right.getPower());
                telemetry.addData("Intake ON (Left Bumper toggle)", intakeOn);
            }
            telemetry.addData("Shooter Target RPM", shooterOn ? SHOOTER_TARGET_RPM : 0.0);
            telemetry.addData("Shooter Actual RPM", shooterActualRpm);
            telemetry.addData("Shooter ON (Right Bumper toggle)", shooterOn);
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
