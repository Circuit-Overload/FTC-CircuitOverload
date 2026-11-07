package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous
public class AutoRoute4 extends LinearOpMode {

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
        shooter.hardwareMapServo("servo_intake");

        waitForStart();

        if (opModeIsActive()) {
          
          path4();

        }
    }

    public void path4() {
        shooter.waitUntilAtSpeed(3.0);
        intake.setPower(1);
        shooter.servoForSeconds(2.0);
        drive.driveStraightInches(0.5, -5);
        sleep(200);
        drive.turnDegreesIMU(0.5, 90, 3.0);
        sleep(200);
        drive.strafeInches(0.5, -8);
        //drive.turnDegreesIMU(0.5, -5, 3.0 );
        drive.driveStraightInches(0.5, 48);
        drive.driveStraightInches(0.5, -48);
        drive.strafeInches(0.5, 8);
        drive.turnDegreesIMU(0.5, -90, 3.0);
        drive.driveStraightInches(0.5, 10);
        shooter.servoForSeconds(2.0);
        shooter.servoForSeconds(2.0);
        drive.strafeInches(0.5, 24);
        sleep(200);
        drive.driveStraightInches(0.5, -99);
        sleep(200);
        drive.strafeInches(0.5, 16);
        shooter.stop();
        intake.stop();


    }
}