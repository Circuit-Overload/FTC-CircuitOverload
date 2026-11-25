// WORKS IN PERSON

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous
    public class Test extends LinearOpMode {

    Movement drive;
    Intake intake;
    Shooter shooter;

    @Override
    public void runOpMode() {
        // Initialize all mechanism classes
        drive = new Movement(this);
        intake = new Intake(this);
        shooter = new Shooter(this);

        // Hardware mapping
        drive.hardwareMapMotors("motor_front_left", "motor_back_left", "motor_front_right", "motor_back_right");
        intake.hardwareMapMotor("motor_intake");
        shooter.hardwareMapShooters("motor_shooter");
        shooter.hardwareMapGate("servo_intake");

        waitForStart();

        if (opModeIsActive()) {
            path1();
        }
    }

    public void path1() {

        drive.turnDegrees(0.5, 90);
        sleep(500);
        drive.turnDegrees(0.5, 90);



        // 1. Move forward 1 inch, then shoot
//        drive.driveStraightInches(0.5, 1.89); // forward 1 inch at start
//        sleep(150);
//        shooter.waitUntilAtSpeed(3.0); // spin up shooter (max 3.0s wait)
//        intake.setPower(0.5); // feed balls into shooter
//        shooter.gateForSeconds(2.0); // open gate for 2.0s to shoot
//
//        // 2. Move 2.5 more inches forward, then strafe left
//        if (opModeIsActive()) {
//            drive.driveStraightInches(0.5, 5); // 2.5 more inches forward after first shot
//            sleep(150);
//        }
//        if (opModeIsActive()) {
//            drive.strafeInches(0.5, -48.0); // TODO: tune left strafe distance (+ = left on your bot)
//            sleep(200);
//        }
//
//        // 3. Move back to intake
//        if (opModeIsActive()) {
//            intake.setPower(1.0); // full intake power to collect
//            drive.driveStraightInches(0.5, -7.0); // TODO: tune backward distance (- = back)
//            sleep(250);
//        }
//
//        // 4. Go to the other side of the same alliance goal (~1 inch from wall) and shoot
//        if (opModeIsActive()) {
//            drive.turnDegreesIMU(0.5, 0.0, 3.0); // TODO: tune turn angle (+ clockwise, - counter-clockwise)
//            sleep(150);
//        }
//        if (opModeIsActive()) {
//            drive.driveStraightInches(0.5, 6);
//            drive.strafeInches(0.5,12);
//            drive.driveStraightInches(0.5, 48.0); // TODO: tune distance toward other side of alliance goal
//            sleep(150);
//        }
//        if (opModeIsActive()) {
//            drive.strafeInches(0.5, 0.0); // TODO: tune strafe so robot sits ~1 in from wall
//            sleep(150);
//        }
//        if (opModeIsActive()) {
//            drive.turnDegreesIMU(0.5, 180, 6.0);
//            drive.strafeInches(0.5, -12);
//            shooter.waitUntilAtSpeed(3.0); // spin up shooter before second shot
//            intake.setPower(0.5); // feed balls into shooter
//            shooter.gateForSeconds(2.0); // open gate for 2.0s to shoot
//        }
//
//        // 5. Park and stop mechanisms
//        if (opModeIsActive()) {
//            drive.strafeInches(0.5, 35);
//        }
//
//
//        shooter.shooterStop();
//        intake.stop();
//        drive.stop();
    }
}