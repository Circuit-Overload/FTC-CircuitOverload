package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class GamePadPractice extends OpMode {

    // Declare motors
    DcMotor frontLeftMotor;
    DcMotor frontRightMotor;
    DcMotor backLeftMotor;
    DcMotor backRightMotor;
    DcMotor intakeMotor;

<<<<<<< HEAD
    boolean previousA = false;
    boolean intakeOn = false;

    @ Override
    public void init () {

        // get motors from driver hub
        hardwareMap.get(DcMotor.class, "motor_left_front");
        hardwareMap.get(DcMotor.class, "motor_right_front");
        hardwareMap.get(DcMotor.class, "motor_left_back");
        hardwareMap.get(DcMotor.class, "motor_right_back");
        hardwareMap.get(DcMotor.class, "motor_intake");

    }

    public void loop () {

        // print gamepad data to driver hub screen
=======
    @Override
    public void init() {
        frontLeftMotor = hardwareMap.get(DcMotor.class, "motor_front_left");
        frontRightMotor = hardwareMap.get(DcMotor.class, "motor_front_right");
        backLeftMotor = hardwareMap.get(DcMotor.class, "motor_back_left");
        backRightMotor = hardwareMap.get(DcMotor.class, "motor_back_right");
        intakeMotor = hardwareMap.get(DcMotor.class, "motor_intake");
    }

    @Override
    public void loop() {
>>>>>>> 72b7b31 (Abhi's code fixed)
        telemetry.addData("y", gamepad1.left_stick_y);
        telemetry.addData("x", gamepad1.left_stick_x);
        telemetry.addData("a", gamepad1.a);

        // variables for gamepad joysticks
        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;

        // power for robot to move
        double frontLeftPower  = y + x;
        double frontRightPower = y - x;
        double backLeftPower   = y - x;
        double backRightPower  = y + x;

        // set robot power using power variables from above
        frontLeftMotor.setPower(frontLeftPower);
        frontRightMotor.setPower(frontRightPower);
        backLeftMotor.setPower(backLeftPower);
        backRightMotor.setPower(backRightPower);

<<<<<<< HEAD
        // make a toggle intake motor
        boolean currentA = gamepad1.a;

        if (currentA && !previousA) {
            intakeOn = !intakeOn;
        }

        if (intakeOn) {
            intakeMotor.setPower(1.0);
        } else {
            intakeMotor.setPower(0.0);
        }

        previousA = currentA;

=======
        if (gamepad1.a) {
            intakeMotor.setPower(1);
        } else {
            intakeMotor.setPower(0);
        }
>>>>>>> 72b7b31 (Abhi's code fixed)
    }
}
