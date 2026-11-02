package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Movement {
    private DcMotor motor_front_left, motor_front_right, motor_back_left, motor_back_right;

    // Switch all these values to ones on the motor
    private final double TICKS_PER_REV = 537.7;     // Adjust for your specific motor (e.g., GoBILDA 19.2:1)
    private final double GEAR_RATIO = 1.0;          // 1.0 if wheels are mounted directly to the motor shaft
    private final double WHEEL_DIAMETER_INCHES = 3.77;
    private final double TRACK_WIDTH_INCHES = 15.0;

    private final double COUNTS_PER_INCH_STRAIGHT = (TICKS_PER_REV * GEAR_RATIO) / (WHEEL_DIAMETER_INCHES * Math.PI);

    // Mecanum wheels slip more during sideways movement.
    // This multiplier accounts for traction loss (typically 1.10 to 1.20).
    private final double STRAFE_SLIP_MULTIPLIER = 1.15;

    // Conversion for strafing sideways
    private final double COUNTS_PER_INCH_STRAFE = COUNTS_PER_INCH_STRAIGHT * STRAFE_SLIP_MULTIPLIER;

    private LinearOpMode opMode;
    private ElapsedTime runtime = new ElapsedTime();

    public Movement(LinearOpMode opMode) {
        this.opMode = opMode;
    }

    public void hardwareMapMotors(String frontLeft, String backLeft, String frontRight, String backRight) {
        motor_front_left = opMode.hardwareMap.get(DcMotor.class, frontLeft);
        motor_back_left = opMode.hardwareMap.get(DcMotor.class, backLeft);
        motor_front_right = opMode.hardwareMap.get(DcMotor.class, frontRight);
        motor_back_right = opMode.hardwareMap.get(DcMotor.class, backRight);
    }

    public void straight(double power) {
        motor_front_left.setPower(power);
        motor_back_left.setPower(power);
        motor_front_right.setPower(power);
        motor_back_right.setPower(power);
    }

    public void stop() {
        motor_front_left.setPower(0);
        motor_back_left.setPower(0);
        motor_front_right.setPower(0);
        motor_back_right.setPower(0);
    }

    public void straightForSeconds(double power, double seconds) {
        runtime.reset();
        while (opMode.opModeIsActive() && runtime.seconds() < seconds) {
            straight(power);
            opMode.telemetry.addData("Moving forward for:", "%.2f seconds", runtime.seconds());
            opMode.telemetry.update();
        }
        stop();
    }

    /**
     * @param power negative power moves robot left, and vice versa
     */
    public void strafe(double power) {
        motor_front_left.setPower(power);
        motor_back_left.setPower(-power);
        motor_front_right.setPower(-power);
        motor_back_right.setPower(power);
    }

    /**
     * @param power negative power moves robot left, and vice versa
     */
    public void strafeForSeconds(double power, double seconds) {
        runtime.reset();
        while (opMode.opModeIsActive() && runtime.seconds() < seconds) {
            strafe(power);
            opMode.telemetry.addData("Strafing for:", "%.2f seconds", runtime.seconds());
            opMode.telemetry.update();
        }
        stop();
    }

    public void turn(double power) {
        motor_front_left.setPower(power);
        motor_back_left.setPower(power);
        motor_front_right.setPower(-power);
        motor_back_right.setPower(-power);
    }

    public void turnForSeconds(double power, double seconds) {
        runtime.reset();
        while (opMode.opModeIsActive() && runtime.seconds() < seconds) {
            turn(power);
            opMode.telemetry.addData("Turning for:", "%.2f seconds", runtime.seconds());
            opMode.telemetry.update();
        }
        stop();
    }

    public void setEncoderMode(String mode) {
        switch (mode.toUpperCase()) {
            case "RUN_TO_POSITION":
                motor_front_right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                motor_front_left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                motor_back_right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                motor_back_left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                break;
            case "RUN_USING_ENCODER":
                motor_front_right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                motor_front_left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                motor_back_right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                motor_back_left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                break;
            case "STOP_AND_RESET_ENCODER":
                motor_front_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                motor_front_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                motor_back_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                motor_back_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                break;
            case "RUN_WITHOUT_ENCODER":
                motor_front_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                motor_front_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                motor_back_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                motor_back_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                break;
            default:
                opMode.telemetry.addData("NOT A PARAMETER", 0);
                opMode.telemetry.update();
                break;
        }
    }

    public void driveForwardTicks(int ticks, double power) {
        setEncoderMode("STOP_AND_RESET_ENCODER");

        motor_front_right.setTargetPosition(ticks);
        motor_front_left.setTargetPosition(ticks);
        motor_back_right.setTargetPosition(ticks);
        motor_back_left.setTargetPosition(ticks);

        setEncoderMode("RUN_TO_POSITION");

        motor_front_right.setPower(power);
        motor_front_left.setPower(power);
        motor_back_right.setPower(power);
        motor_back_left.setPower(power);

        while (opMode.opModeIsActive() && (motor_front_left.isBusy() && motor_front_right.isBusy())) {
            opMode.telemetry.addData("Driving to target ticks", ticks);
            opMode.telemetry.update();
        }

        stop();
    }

    public void driveStraightInches(double power, double inches) {
        int targetTicks = (int) (Math.round(inches * COUNTS_PER_INCH_STRAIGHT));

        setEncoderMode("STOP_AND_RESET_ENCODER");

        motor_front_right.setTargetPosition(targetTicks);
        motor_front_left.setTargetPosition(targetTicks);
        motor_back_right.setTargetPosition(targetTicks);
        motor_back_left.setTargetPosition(targetTicks);

        setEncoderMode("RUN_TO_POSITION");

        motor_front_right.setPower(power);
        motor_front_left.setPower(power);
        motor_back_right.setPower(power);
        motor_back_left.setPower(power);

        while (opMode.opModeIsActive() &&
                (motor_front_left.isBusy() || motor_front_right.isBusy() ||
                        motor_back_left.isBusy() || motor_back_right.isBusy())) {

            opMode.telemetry.addData("Target Inches", inches);
            opMode.telemetry.addData("Current FL Ticks", motor_front_left.getCurrentPosition());
            opMode.telemetry.addData("Target Ticks", targetTicks);
            opMode.telemetry.update();
        }

        stop();
        setEncoderMode("RUN_USING_ENCODER");
    }

    public void turnDegrees(double power, double degrees) {
        if (opMode.opModeIsActive()) {
            double turnCircumference = TRACK_WIDTH_INCHES * Math.PI;
            double targetInches = (turnCircumference * degrees) / 360.0;
            int targetTicks = (int) (targetInches * COUNTS_PER_INCH_STRAIGHT);

            motor_front_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor_back_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor_front_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor_back_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            motor_front_left.setTargetPosition(targetTicks);
            motor_back_left.setTargetPosition(targetTicks);
            motor_front_right.setTargetPosition(-targetTicks);
            motor_back_right.setTargetPosition(-targetTicks);

            motor_front_left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor_back_left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor_front_right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor_back_right.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            motor_front_left.setPower(Math.abs(power));
            motor_back_left.setPower(Math.abs(power));
            motor_front_right.setPower(Math.abs(power));
            motor_back_right.setPower(Math.abs(power));

            while (opMode.opModeIsActive() &&
                    (motor_front_left.isBusy() || motor_back_left.isBusy() || motor_front_right.isBusy() || motor_back_right.isBusy())) {

                opMode.telemetry.addData("Target Ticks", targetTicks);
                opMode.telemetry.addData("LF Pos", motor_front_left.getCurrentPosition());
                opMode.telemetry.addData("RF Pos", motor_front_right.getCurrentPosition());
                opMode.telemetry.update();
            }

            stop();

            motor_front_left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_back_left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_front_right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_back_right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public void strafeInches(double power, double inches) {
        if (opMode.opModeIsActive()) {
            int targetTicks = (int) (inches * COUNTS_PER_INCH_STRAFE);

            motor_front_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor_front_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor_back_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor_back_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            motor_front_left.setTargetPosition(targetTicks);
            motor_front_right.setTargetPosition(-targetTicks);
            motor_back_left.setTargetPosition(-targetTicks);
            motor_back_right.setTargetPosition(targetTicks);

            motor_front_left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor_front_right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor_back_left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor_back_right.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            motor_front_left.setPower(Math.abs(power));
            motor_front_right.setPower(Math.abs(power));
            motor_back_left.setPower(Math.abs(power));
            motor_back_right.setPower(Math.abs(power));

            while (opMode.opModeIsActive() &&
                    (motor_front_left.isBusy() || motor_front_right.isBusy() ||
                            motor_back_left.isBusy() || motor_back_right.isBusy())) {

                opMode.telemetry.addData("Strafing Inches", inches);
                opMode.telemetry.addData("FL Ticks", motor_front_left.getCurrentPosition());
                opMode.telemetry.addData("FR Ticks", motor_front_right.getCurrentPosition());
                opMode.telemetry.update();
            }

            stop();

            motor_front_left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_front_right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_back_left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor_back_right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }
    public void diagonalForSeconds(double power, double angle, double seconds) {
        runtime.reset();

        // Convert angle to radians
        double radians = Math.toRadians(angle);

        // Calculate the vector components for mecanum wheels
        // cos(angle) controls forward/backward, sin(angle) controls strafing left/right
        double forward = Math.cos(radians);
        double strafe = Math.sin(radians);

        // Standard mecanum mixing formula for diagonal movement
        double frontLeftPower  = (forward + strafe) * power;
        double frontRightPower = (forward - strafe) * power;
        double backLeftPower   = (forward - strafe) * power;
        double backRightPower  = (forward + strafe) * power;

        // Normalize powers so none exceed 1.0
        double max = Math.max(Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                Math.max(Math.abs(backLeftPower), Math.abs(backRightPower)));
        if (max > 1.0) {
            frontLeftPower /= max;
            frontRightPower /= max;
            backLeftPower /= max;
            backRightPower /= max;
        }

        while (opMode.opModeIsActive() && runtime.seconds() < seconds) {
            motor_front_left.setPower(frontLeftPower);
            motor_front_right.setPower(frontRightPower);
            motor_back_left.setPower(backLeftPower);
            motor_back_right.setPower(backRightPower);

            opMode.telemetry.addData("Driving Diagonally, Angle:", angle);
            opMode.telemetry.addData("Time left:", "%.2f", seconds - runtime.seconds());
            opMode.telemetry.update();
        }
        stop();
    }

    /**
     * Drives diagonally using encoders for a precise number of inches at a specific angle.
     *
     * @param power  Overall speed/power
     * @param angle  Angle in degrees (e.g., 45 for up-right, 135 for down-right)
     * @param inches Distance to travel
     */
    public void driveDiagonalInches(double power, double angle, double inches) {
        if (opMode.opModeIsActive()) {
            double radians = Math.toRadians(angle);
            double forward = Math.cos(radians);
            double strafe = Math.sin(radians);

            // Factor in slip for diagonal/strafe movement
            double effectiveInches = inches * ((Math.abs(strafe) > 0.1) ? STRAFE_SLIP_MULTIPLIER : 1.0);
            int targetTicks = (int) (effectiveInches * COUNTS_PER_INCH_STRAIGHT);

            // Calculate individual motor target adjustments based on the vector
            int flTarget = (int) (targetTicks * (forward + strafe));
            int frTarget = (int) (targetTicks * (forward - strafe));
            int blTarget = (int) (targetTicks * (forward - strafe));
            int brTarget = (int) (targetTicks * (forward + strafe));

            setEncoderMode("STOP_AND_RESET_ENCODER");

            motor_front_left.setTargetPosition(flTarget);
            motor_front_right.setTargetPosition(frTarget);
            motor_back_left.setTargetPosition(blTarget);
            motor_back_right.setTargetPosition(brTarget);

            setEncoderMode("RUN_TO_POSITION");

            double flPower = (forward + strafe) * Math.abs(power);
            double frPower = (forward - strafe) * Math.abs(power);
            double blPower = (forward - strafe) * Math.abs(power);
            double brPower = (forward + strafe) * Math.abs(power);

            motor_front_left.setPower(flPower);
            motor_front_right.setPower(frPower);
            motor_back_left.setPower(blPower);
            motor_back_right.setPower(brPower);

            while (opMode.opModeIsActive() &&
                    (motor_front_left.isBusy() || motor_front_right.isBusy() ||
                            motor_back_left.isBusy() || motor_back_right.isBusy())) {

                opMode.telemetry.addData("Diagonal Angle", angle);
                opMode.telemetry.addData("Target Ticks FL", flTarget);
                opMode.telemetry.addData("Current FL", motor_front_left.getCurrentPosition());
                opMode.telemetry.update();
            }

            stop();
            setEncoderMode("RUN_USING_ENCODER");
        }
    }

}

