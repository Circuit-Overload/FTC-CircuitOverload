
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Movement {

    private DcMotor motor_front_left;
    private DcMotor motor_front_right;
    private DcMotor motor_back_left;
    private DcMotor motor_back_right;

    private LinearOpMode opMode;
    private IMU imu;

    private ElapsedTime runtime = new ElapsedTime();

    // ================================================
    // MOVEMENT CALIBRATION
    // THESE ARE STARTING ESTIMATES. TEST YOUR ROBOT.
    // ================================================

    // Estimated inches per second at full motor power.
    // You MUST calibrate these values on your robot.
    private double FORWARD_INCHES_PER_SECOND = 24.0;
    private double STRAFE_INCHES_PER_SECOND = 18.0;

    // IMU turn settings
    private final double TURN_TOLERANCE = 1.5;
    private final double TURN_MIN_POWER = 0.12;
    private final double TURN_P = 0.02;

    public Movement(LinearOpMode opMode) {
        this.opMode = opMode;
    }

    // ================================================
    // HARDWARE INITIALIZATION
    // ================================================

    public void hardwareMapMotors(
            String frontLeft,
            String backLeft,
            String frontRight,
            String backRight) {

        motor_front_left =
                opMode.hardwareMap.get(DcMotor.class, frontLeft);

        motor_back_left =
                opMode.hardwareMap.get(DcMotor.class, backLeft);

        motor_front_right =
                opMode.hardwareMap.get(DcMotor.class, frontRight);

        motor_back_right =
                opMode.hardwareMap.get(DcMotor.class, backRight);

        // Motor directions
        motor_front_left.setDirection(DcMotor.Direction.REVERSE);
        motor_back_left.setDirection(DcMotor.Direction.REVERSE);

        motor_front_right.setDirection(DcMotor.Direction.FORWARD);
        motor_back_right.setDirection(DcMotor.Direction.FORWARD);

        // Brake when stopped
        motor_front_left.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE);

        motor_front_right.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE);

        motor_back_left.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE);

        motor_back_right.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE);

        // Initialize IMU
        imu = opMode.hardwareMap.get(IMU.class, "imu");

        imu.initialize(new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.UP,
                        RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
                )
        ));

        imu.resetYaw();

        stop();
    }

    // ================================================
    // BASIC MOTOR CONTROL
    // ================================================

    private double safePower(double power) {
        return Range.clip(power, -1.0, 1.0);
    }

    public void stop() {

        if (motor_front_left == null) return;

        motor_front_left.setPower(0);
        motor_front_right.setPower(0);
        motor_back_left.setPower(0);
        motor_back_right.setPower(0);
    }

    public void straight(double power) {

        power = safePower(power);

        motor_front_left.setPower(power);
        motor_front_right.setPower(power);
        motor_back_left.setPower(power);
        motor_back_right.setPower(power);
    }

    // ================================================
    // SHARED TIMED MOVEMENT
    // ================================================

    private void runForTime(
            double fl,
            double fr,
            double bl,
            double br,
            double seconds) {

        if (!opMode.opModeIsActive() || seconds <= 0) {
            stop();
            return;
        }

        seconds = Math.min(seconds, 25.0);

        ElapsedTime timer = new ElapsedTime();

        try {

            while (opMode.opModeIsActive() &&
                    timer.seconds() < seconds) {

                motor_front_left.setPower(safePower(fl));
                motor_front_right.setPower(safePower(fr));
                motor_back_left.setPower(safePower(bl));
                motor_back_right.setPower(safePower(br));

                opMode.telemetry.addData(
                        "Movement Time",
                        "%.2f / %.2f",
                        timer.seconds(), seconds);

                opMode.telemetry.update();

                opMode.idle();
            }

        } finally {
            stop();
        }
    }

    // ================================================
    // STRAIGHT MOVEMENT BY TIME
    // ================================================

    public void straightForSeconds(double power, double seconds) {

        runForTime(
                power,
                power,
                power,
                power,
                seconds
        );
    }

    // ================================================
    // STRAIGHT MOVEMENT BY INCHES
    // Positive = forward
    // Negative = backward
    // ================================================

    public void driveStraightInches(double power, double inches) {

        if (Math.abs(power) < 0.05) {
            stop();
            return;
        }

        double speed =
                FORWARD_INCHES_PER_SECOND * Math.abs(power);

        double seconds = Math.abs(inches) / speed;

        double direction = Math.signum(inches);

        runForTime(
                direction * Math.abs(power),
                direction * Math.abs(power),
                direction * Math.abs(power),
                direction * Math.abs(power),
                seconds
        );
    }

    // ================================================
    // STRAFING
    // Positive = right
    // Negative = left
    // ================================================

    public void strafe(double power) {

        power = safePower(power);

        motor_front_left.setPower(power);
        motor_back_left.setPower(-power);

        motor_front_right.setPower(-power);
        motor_back_right.setPower(power);
    }

    public void strafeForSeconds(double power, double seconds) {

        runForTime(
                power,
                -power,
                -power,
                power,
                seconds
        );
    }

    // ================================================
    // STRAFING BY INCHES
    // Positive = right
    // Negative = left
    // ================================================

    public void strafeInches(double power, double inches) {

        if (Math.abs(power) < 0.05) {
            stop();
            return;
        }

        double speed =
                STRAFE_INCHES_PER_SECOND * Math.abs(power);

        double seconds = Math.abs(inches) / speed;

        double direction = Math.signum(inches);
        double movementPower = Math.abs(power) * direction;

        runForTime(
                movementPower,
                -movementPower,
                -movementPower,
                movementPower,
                seconds
        );
    }

    // ================================================
    // TURNING
    // Positive = clockwise
    // Negative = counterclockwise
    // ================================================

    public void turn(double power) {

        power = safePower(power);

        motor_front_left.setPower(power);
        motor_back_left.setPower(power);

        motor_front_right.setPower(-power);
        motor_back_right.setPower(-power);
    }

    public void turnForSeconds(double power, double seconds) {

        runForTime(
                power,
                -power,
                power,
                -power,
                seconds
        );
    }

    // ================================================
    // IMU TURNING
    // Positive degrees = clockwise
    // Negative degrees = counterclockwise
    // ================================================

    public void turnDegreesIMU(
            double maxPower,
            double degrees,
            double timeoutS) {

        if (!opMode.opModeIsActive() ||
                maxPower <= 0 ||
                timeoutS <= 0) {

            stop();
            return;
        }

        maxPower = Range.clip(
                Math.abs(maxPower), 0.0, 1.0);

        double startYaw =
                imu.getRobotYawPitchRollAngles()
                        .getYaw(AngleUnit.DEGREES);

        double targetYaw = startYaw - degrees;

        ElapsedTime timer = new ElapsedTime();

        try {

            while (opMode.opModeIsActive() &&
                    timer.seconds() < timeoutS) {

                double currentYaw =
                        imu.getRobotYawPitchRollAngles()
                                .getYaw(AngleUnit.DEGREES);

                double error = AngleUnit.normalizeDegrees(
                        targetYaw - currentYaw
                );

                if (Math.abs(error) < TURN_TOLERANCE) {
                    break;
                }

                double power = Range.clip(
                        error * TURN_P,
                        -maxPower,
                        maxPower
                );

                if (Math.abs(power) < TURN_MIN_POWER) {
                    power = Math.copySign(
                            TURN_MIN_POWER, power);
                }

                power = Range.clip(
                        power, -maxPower, maxPower);

                // Original turn direction convention
                turn(-power);

                opMode.telemetry.addData(
                        "Target Heading", targetYaw);

                opMode.telemetry.addData(
                        "Current Heading", currentYaw);

                opMode.telemetry.addData(
                        "Heading Error", error);

                opMode.telemetry.addData(
                        "Time Remaining",
                        timeoutS - timer.seconds());

                opMode.telemetry.update();

                opMode.idle();
            }

        } finally {
            stop();
        }
    }

    // ================================================
    // ENCODER TURN COMPATIBILITY
    // Uses IMU instead of encoders.
    // ================================================

    public void turnDegrees(double power, double degrees) {

        double timeout = Math.max(
                2.0,
                Math.abs(degrees) / 30.0 + 1.0
        );

        turnDegreesIMU(power, degrees, timeout);
    }

    // ================================================
    // ENCODER MODE COMPATIBILITY
    // No encoder cables = no encoder operation.
    // ================================================

    public void setEncoderMode(String mode) {

        opMode.telemetry.addData(
                "Encoder Mode",
                "Ignored - robot uses timed movement");

        opMode.telemetry.update();
    }

    // ================================================
    // TICKS COMPATIBILITY
    // Converts approximate ticks to inches.
    // Does NOT read encoder positions.
    // ================================================

    public void driveForwardTicks(int ticks, double power) {

        // Approximate conversion for the original
        // 537.7 ticks/rev and 4.094-inch wheel.
        double countsPerInch =
                537.7 / (4.094 * Math.PI);

        double inches = ticks / countsPerInch;

        driveStraightInches(power, inches);
    }

    // ================================================
    // DIAGONAL MOVEMENT BY TIME
    //
    // 0 = forward
    // 90 = right
    // -90 = left
    // 180 = backward
    // ================================================

    public void diagonalForSeconds(
            double power,
            double angle,
            double seconds) {

        double radians = Math.toRadians(angle);

        double forward = Math.cos(radians);
        double sideways = Math.sin(radians);

        double fl = forward + sideways;
        double fr = forward - sideways;
        double bl = forward - sideways;
        double br = forward + sideways;

        double max = Math.max(
                Math.max(Math.abs(fl), Math.abs(fr)),
                Math.max(Math.abs(bl), Math.abs(br))
        );

        if (max > 0) {
            fl /= max;
            fr /= max;
            bl /= max;
            br /= max;
        }

        power = safePower(power);

        runForTime(
                fl * power,
                fr * power,
                bl * power,
                br * power,
                seconds
        );
    }

    // ================================================
    // DIAGONAL MOVEMENT BY INCHES
    //
    // 0 = forward
    // 90 = right
    // -90 = left
    // 180 = backward
    // ================================================

    public void driveDiagonalInches(
            double power,
            double angle,
            double inches) {

        if (Math.abs(power) < 0.05) {
            stop();
            return;
        }

        double radians = Math.toRadians(angle);

        double forward = Math.cos(radians);
        double sideways = Math.sin(radians);

        // Estimate combined movement speed.
        double forwardSpeed =
                FORWARD_INCHES_PER_SECOND * forward;

        double sidewaysSpeed =
                STRAFE_INCHES_PER_SECOND * sideways;

        double speed = Math.sqrt(
                forwardSpeed * forwardSpeed +
                sidewaysSpeed * sidewaysSpeed
        );

        speed *= Math.abs(power);

        if (speed < 0.01) {
            stop();
            return;
        }

        double seconds = Math.abs(inches) / speed;

        // Negative distance reverses the entire direction.
        double direction = Math.signum(inches);

        double fl = (forward + sideways) * direction;
        double fr = (forward - sideways) * direction;
        double bl = (forward - sideways) * direction;
        double br = (forward + sideways) * direction;

        double max = Math.max(
                Math.max(Math.abs(fl), Math.abs(fr)),
                Math.max(Math.abs(bl), Math.abs(br))
        );

        if (max > 0) {
            fl /= max;
            fr /= max;
            bl /= max;
            br /= max;
        }

        runForTime(
                fl * Math.abs(power),
                fr * Math.abs(power),
                bl * Math.abs(power),
                br * Math.abs(power),
                seconds
        );
    }
}
