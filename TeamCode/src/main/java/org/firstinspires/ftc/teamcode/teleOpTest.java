package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@TeleOp(name="Simple Straight Drive Test", group="TeleOp")
public class teleOpTest extends LinearOpMode {

    Movement drive;

    @Override
    public void runOpMode() {
        drive = new Movement(this);
        drive.hardwareMapMotors("motor_front_left", "motor_back_left", "motor_front_right", "motor_back_right");

        telemetry.addLine("Initialized. Push left stick forward/backward.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Read left stick Y (negated so pushing forward is positive power)
            double drivePower = -gamepad1.left_stick_y;

            // Apply power uniformly across all four wheels to go straight
            drive.straight(drivePower);

            telemetry.addData("Drive Power", drivePower);
            telemetry.update();
        }
    }
}