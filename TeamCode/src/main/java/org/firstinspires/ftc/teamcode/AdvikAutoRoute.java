package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous
public class AdvikAutoRoute extends LinearOpMode {

    Movement drive;
    Intake intake;
    Shooter shooter;

    @Override

    public void runOpMode() {
        // Initialize all mechanism classes
        drive = new Movement(this);
        intake = new Intake(this);
        shooter = new Shooter(this);

        //hardware mapping
        drive.hardwareMapMotors("motor_front_left", "motor_back_left", "motor_front_right", "motor_back_right");
        intake.hardwareMapMotor("motor_intake");
        shooter.hardwareMapShooters("motor_shooter");

        waitForStart();

        if (opModeIsActive()) {
            path1();
        }

        public void path1() {
            shooter.fireForSeconds(1.5, 2.0);  
            rotateForDegrees(-0.5, -90);
        }

    }

}