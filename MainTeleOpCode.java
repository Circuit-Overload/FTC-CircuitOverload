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

    // === NEW: Turret Motor Declaration (Commented out until ready) ===
    // private DcMotor motor_turret;

    // Shooter closed-loop velocity control (holds RPM despite friction / battery sag)
    private static final double SHOOTER_TARGET_RPM = 2750.0;
    private static final double SHOOTER_TICKS_PER_REV = 28.0;
    private static final double SHOOTER_TARGET_TPS = SHOOTER_TARGET_RPM * SHOOTER_TICKS_PER_REV / 60.0; 
    private static final double SHOOTER_DIRECTION = 1.0;

    // Shooter velocity PIDF gains
    private static final double SHOOTER_P = 30.0;
    private static final double SHOOTER_I = 0.0;
    private static final double SHOOTER_D = 2.5;  
    private static final double SHOOTER_F = 15.0;   
    private static final double SHOOTER_AT_SPEED_TOLERANCE_RPM = 150.0;

    // Limelight Declaration
    private Limelight3A limelight;

    // Toggle Tracking Variables
    private boolean intakeOn = false;
    private boolean lastIntakeButtonState = false;

    private boolean intakeReverse = false;
    private boolean lastReverseButtonState = false;

    private boolean shooterOn = false;
    private boolean lastShooterButtonState = false;

    // Speed toggle: press B on gamepad1 to switch between full and 65% speed
    private boolean slowMode = false;
    private boolean lastM1State = false;

    // === NEW: Alliance Selection Variables for AprilTag Turret (Commented out) ===
    // private boolean isRedAlliance = true; // Default to Red side

    // Servo Angle Scaling for a 300-degree servo
    private static final double SERVO_START_POS = 50.0 / 1800.0;
    private static final double SERVO_DOWN_POS  = 05.0 / 1800.0;
   
    @Override
    public void runOpMode() {

        // Hardware Mapping
        motor_front_left = hardwareMap.get(DcMotor.class, "motor_front_left");
        motor_back_left = hardwareMap.get(DcMotor.class, "motor_back_left");
        motor_front_right = hardwareMap.get(DcMotor.class, "motor_front_right");
        motor_back_right = hardwareMap.get(DcMotor.class, "motor_back_right");

        motor_intake = hardwareMap.get(DcMotor.class, "motor_intake");
        motor_shooter = hardwareMap.get(DcMotorEx.class, "motor_shooter");
        motor_shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor_shooter.setVelocityPIDFCoefficients(SHOOTER_P, SHOOTER_I, SHOOTER_D, SHOOTER_F);
        servo_intake = hardwareMap.get(Servo.class, "servo_intake");

        // === NEW: Turret Motor Mapping (Commented out) ===
        // motor_turret = hardwareMap.get(DcMotor.class, "motor_turret");
        // motor_turret.setDirection(DcMotor.Direction.FORWARD);
        // motor_turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Map Limelight
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
            double tx = 0.0; 

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

            if (abs(left_y) <= 0.05) left_y = 0;
            if (abs(left_x) <= 0.05) left_x = 0;
            if (abs(right_x) <= 0.05) right_x = 0;

            boolean currentM1State = gamepad1.b;
            if (currentM1State && !lastM1State) {
                slowMode = !slowMode;
            }
            lastM1State = currentM1State;

            if (gamepad1.left_bumper && targetValid) {
                double steerKp = 0.03; 
                right_x = tx * steerKp;
            }

            double flPower = left_y + left_x + right_x;
            double blPower = left_y - left_x + right_x;
            double frPower = left_y - left_x - right_x;
            double brPower = left_y + left_x - right_x;

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
            motor_front_right.setPower(frPower * acceleration * speedMultiplier);
            motor_back_left.setPower(blPower * acceleration * speedMultiplier);
            motor_back_right.setPower(brPower * acceleration * speedMultiplier);

            // -------------------------------------------------------------
            // 3. INTAKE & SHOOTER MOTOR TOGGLES
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

            boolean currentReverseButtonState = gamepad2.dpad_left;
            if (currentReverseButtonState && !lastReverseButtonState) {
                intakeReverse = !intakeReverse;
            }
            lastReverseButtonState = currentReverseButtonState;

            double intakePower = intakeOn ? 0.667 : 0.0;
            motor_intake.setPower(intakeReverse ? -intakePower : intakePower);
            motor_shooter.setVelocity(shooterOn ? SHOOTER_DIRECTION * SHOOTER_TARGET_TPS : 0.0);

            // -------------------------------------------------------------
            // 4. SERVO POSITION CONTROL
            // -------------------------------------------------------------
            boolean servoDownHeld = gamepad2.left_trigger > 0.5 || gamepad2.right_trigger > 0.5;
            if (servoDownHeld) {
                servo_intake.setPosition(SERVO_DOWN_POS);
            } else {
                servo_intake.setPosition(SERVO_START_POS);
            }

            // =============================================================
            // 5. NEW: APRILTAG TURRET SEARCH & LOCK-ON LOGIC (Commented Out)
            // =============================================================
            /*
            // Switch Limelight to AprilTag pipeline if needed (e.g. Pipeline 1)
            // limelight.pipelineSwitch(1);

            // Select Alliance via Gamepad 2 (X = Blue, B = Red)
            if (gamepad2.x) {
                isRedAlliance = false;
            } else if (gamepad2.b) {
                isRedAlliance = true;
            }

            boolean correctTagFound = false;
            long detectedId = -1;
            double tagTx = 0.0;

            if (result != null && result.isValid()) {
                detectedId = result.getFiducialID(); // Gets AprilTag ID number
                tagTx = result.getTx();

                // Differentiate Red vs Blue tag ID ranges (Update numbers per your manual)
                if (isRedAlliance && detectedId >= 1 && detectedId <= 5) {
                    correctTagFound = true; // Red alliance tag found
                } else if (!isRedAlliance && detectedId >= 6 && detectedId <= 10) {
                    correctTagFound = true; // Blue alliance tag found
                }
            }

            double turretPower = 0.0;
            if (correctTagFound) {
                // Lock-on Proportional Control
                double turretKp = 0.025;
                turretPower = tagTx * turretKp;
                if (turretPower > 0 && turretPower < 0.08) turretPower = 0.08;
                if (turretPower < 0 && turretPower > -0.08) turretPower = -0.08;
            } else {
                // Search Mode: Spin in circles looking for the right alliance tag
                turretPower = 0.3;
            }

            // motor_turret.setPower(turretPower);
            */

            // -------------------------------------------------------------
            // 6. TELEMETRY DISPLAY
            // -------------------------------------------------------------
            double shooterVelocityTps = motor_shooter.getVelocity();
            double shooterActualRpm = Math.abs(shooterVelocityTps) * 60.0 / SHOOTER_TICKS_PER_REV;
            boolean shooterAtSpeed = shooterOn
                    && Math.abs(shooterActualRpm - SHOOTER_TARGET_RPM) <= SHOOTER_AT_SPEED_TOLERANCE_RPM;

            if (slowMode) {
                telemetry.addLine("*** SLOW MODE ON (65%) ***");
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
