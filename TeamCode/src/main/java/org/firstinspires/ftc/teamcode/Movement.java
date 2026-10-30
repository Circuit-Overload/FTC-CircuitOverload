package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Movement {
    private DcMotor motor_front_left, motor_front_right, motor_back_left, motor_back_right;



    // switch all these values to ones on the motor
    private final double TICKS_PER_REV = 537.7;     // Adjust for your specific motor (e.g., GoBILDA 19.2:1)
    private final double GEAR_RATIO = 1.0;          // 1.0 if wheels are mounted directly to the motor shaft
    private final double WHEEL_DIAMETER_INCHES = 3.77;

    final double COUNTS_PER_INCH = (TICKS_PER_REV * GEAR_RATIO) / (WHEEL_DIAMETER_INCHES * Math.PI);

    private LinearOpMode opMode;

    private ElapsedTime runtime  = new ElapsedTime();

    public Movement(LinearOpMode opMode) {
        this.opMode = opMode;

    }



    public void hardwareMapMotors (String frontLeft, String backLeft, String frontRight, String backRight) {
        motor_front_left = opMode.hardwareMap.get(DcMotor.class, frontLeft);
        motor_back_left = opMode.hardwareMap.get(DcMotor.class, backLeft);
        motor_front_right = opMode.hardwareMap.get(DcMotor.class, frontRight);
        motor_back_right = opMode.hardwareMap.get(DcMotor.class, backRight);
    }



    public void straight (double power) {
        motor_front_left.setPower(power);
        motor_back_left.setPower(power);
        motor_front_right.setPower(power);
        motor_back_right.setPower(power);

    }

    public void stop () {
        motor_front_left.setPower(0);
        motor_back_left.setPower(0);
        motor_front_right.setPower(0);
        motor_back_right.setPower(0);
    }

    public void straightForSeconds (double power, double seconds) {

        runtime.reset();

        while (opMode.opModeIsActive() && runtime.seconds() < seconds) {

            straight(power);

                opMode.telemetry.addData("Moving forward for:", "%.2f seconds", runtime.seconds());
            opMode.telemetry.update();

        }

        stop();




    }


    /**
    * @param power              negative power moves robot left, and vice versa
    */
    public void strafe (double power) {
        motor_front_left.setPower(power);
        motor_back_left.setPower(-power);
        motor_front_right.setPower(-power);
        motor_back_right.setPower(power);
    }


    /**
     * @param power              negative power moves robot left, and vice versa
     */
    public void strafeForSeconds (double power, double seconds) {

        runtime.reset();

        while (opMode.opModeIsActive() && runtime.seconds() < seconds) {

            strafe(power);

            opMode.telemetry.addData("Strafing for:", "%.2f seconds", runtime.seconds());
            opMode.telemetry.update();

        }

        stop();

    }

    public void turn (double power) {
        motor_front_left.setPower(power);
        motor_back_left.setPower(power);
        motor_front_right.setPower(-power);
        motor_back_right.setPower(-power);
    }

    public void turnForSeconds (double power, double seconds) {
        runtime.reset();

        while (opMode.opModeIsActive() && runtime.seconds() < seconds) {

            turn(power);

            opMode.telemetry.addData("Turning for:", "%.2f seconds", runtime.seconds());
            opMode.telemetry.update();

        }

        stop();
    }

    public void setEncoderMode (String mode) {
        switch (mode.toUpperCase()){
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
                System.out.println("NOT A PARAMETER");
                opMode.telemetry.addData("NOT A PARAMETER", 0);
                opMode.telemetry.update();






        }


    }

    public void driveForwardTicks (int ticks, double power) {

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

        // Crucial: Use opMode.opModeIsActive() so the loop terminates if someone hits STOP
        while (opMode.opModeIsActive() && (motor_front_left.isBusy() && motor_front_right.isBusy())) {
            opMode.telemetry.addData("Driving to target", ticks);
            opMode.telemetry.update();
        }

        // Stop motors
        motor_front_right.setPower(0);
        motor_front_left.setPower(0);
        motor_back_right.setPower(0);
        motor_back_left.setPower(0);



    }




    public void driveStraightInches(double power, double inches) {
        // 1. Calculate target encoder ticks
        int targetTicks = (int)(Math.round(inches * COUNTS_PER_INCH));

        // 2. Clear out old encoder values
        setEncoderMode("STOP_AND_RESET_ENCODER");

        // 3. Set the target positions relative to current 0 position
        motor_front_right.setTargetPosition(targetTicks);
        motor_front_left.setTargetPosition(targetTicks);
        motor_back_right.setTargetPosition(targetTicks);
        motor_back_left.setTargetPosition(targetTicks);

        // 4. Change mode to run to those positions
        setEncoderMode("RUN_TO_POSITION");

        // 5. Turn on power (Hub limits speed automatically to reach destination smoothly)
        motor_front_right.setPower(power);
        motor_front_left.setPower(power);
        motor_back_right.setPower(power);
        motor_back_left.setPower(power);

        // 6. Loop and block until the wheels arrive at their destination
        // Using 'opMode.opModeIsActive()' ensures safety if someone stops the match early
        while (opMode.opModeIsActive() &&
                (motor_front_left.isBusy() && motor_front_right.isBusy() &&
                        motor_back_left.isBusy() && motor_back_right.isBusy())) {

            opMode.telemetry.addData("Target Inches", inches);
            opMode.telemetry.addData("Current FL Ticks", motor_front_left.getCurrentPosition());
            opMode.telemetry.addData("Target Ticks", targetTicks);
            opMode.telemetry.update();
        }

        // 7. Stop power to all motors after arriving
        motor_front_right.setPower(0);
        motor_front_left.setPower(0);
        motor_back_right.setPower(0);
        motor_back_left.setPower(0);

        // Optional: Switch back to normal speed regulation mode
        setEncoderMode("RUN_USING_ENCODER");
    }







}
