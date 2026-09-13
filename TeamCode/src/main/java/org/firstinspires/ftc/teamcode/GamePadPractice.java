package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp

public class GamePadPractice extends OpMode {

    DcMotor frontLeftMotor;
    DcMotor frontRightMotor;
    DcMotor backLeftMotor;
    DcMotor backRightMotor;
    DcMotor intakeMotor;

    @ Override
    public void init () {

        hardwareMap.get(DcMotor.class, "motor_left_front");
        hardwareMap.get(DcMotor.class, "motor_right_front");
        hardwareMap.get(DcMotor.class, "motor_left_back");
        hardwareMap.get(DcMotor.class, "motor_right_back");
        hardwareMap.get(DcMotor.class, "motor_intake");

        boolean

    }

    public void loop () {


        telemetry.addData("y", gamepad1.left_stick_y);
        telemetry.addData("x", gamepad1.left_stick_x);
        telemetry.addData("a", gamepad1.a);

        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;

        double frontLeftPower  = y + x;
        double frontRightPower = y - x;
        double backLeftPower   = y - x;
        double backRightPower  = y + x;

        frontLeftMotor.setPower(frontLeftPower);
        frontRightMotor.setPower(frontLeftPower);
        backLeftMotor.setPower(frontLeftPower);
        backRightMotor.setPower(frontLeftPower);

        if (gamepad1.a) {
            intakeMotor.setPower(1);
        }


    }
}
