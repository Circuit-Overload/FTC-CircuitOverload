package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Movement {
    private DcMotor motor_front_left, motor_front_right, motor_back_left, motor_back_right;

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






}
