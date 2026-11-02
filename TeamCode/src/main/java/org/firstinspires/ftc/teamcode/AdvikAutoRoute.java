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

            //Not Finished
            path1();
            //Finished
            // path2();
        }
    }

    //Not Finished

    public void path1() {
        shooter.fireForSeconds(1.5, 2.0);
        drive.driveStraightInches(0.5, 5);
        sleep(200);
        drive.turnDegreesIMU(0.5, -90, 3.0); // IMU turn: -90 = left 90 deg
        sleep(200);
        drive.strafeInches(0.5, -5);   
        intake.setPower(0.5);            
        drive.driveStraightInches(0.5, 48); 
        intake.stop();                      // stop intake once drive finishes
    }

    //Finished

    public void path2() {

    drive.strafeInches(0.5, -24);
    sleep(200);
    drive.driveStraightInches(0.5, 99);
    sleep(200);
    drive.strafeInches(0.5, -16);

    }

}


