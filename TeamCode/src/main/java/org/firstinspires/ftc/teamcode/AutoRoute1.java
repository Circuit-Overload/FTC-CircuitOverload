// WORKS IN PERSON

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous
public class AutoRoute1 extends LinearOpMode {

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
        // 1. Move forward and shoot first batch (keep working start)
        drive.driveStraightInches(0.5, 1.89); // forward ~1.89 in at start
        sleep(150);
        shooter.waitUntilAtSpeed(3.0); // spin up shooter
        intake.setPower(0.5); // feed balls into shooter
        shooter.gateForSeconds(2.0); // open gate for 2.0s to shoot
        if (!opModeIsActive()) return;

        // 2. Move forward, strafe, and intake balls (keep working intake sequence)
        drive.driveStraightInches(0.5, 5); // move forward after first shot
        sleep(150);
        drive.strafeInches(0.5, -48.0); // strafe across to intake line
        sleep(200);
        intake.setPower(1.0); // full intake power to collect balls
        drive.driveStraightInches(0.5, -7.0); // drive back to intake balls
        sleep(250);
        if (!opModeIsActive()) return;

        // 3. Go 8 more inches forward toward the other side, turn 90 deg right (non-IMU), line up to shoot
        intake.setPower(0.3); // gentle feed/hold while moving to shoot spot
        drive.driveStraightInches(0.5, 6); // clear intake position
        drive.strafeInches(0.5, 12); // line up before crossing
        drive.driveStraightInches(0.5, 89.0); // +8 in added to 72.0 in so it reaches the other side
        sleep(150);

        drive.turnDegrees(0.4, -80); // 90 deg turn using encoders (no IMU)
        drive.driveStraightInches(0.5, 33); // +8 in added to 72.0 in so it reaches th
        drive.turnDegrees(0.4, -77); // 90 deg turn using encoders (
        drive.strafeInches(0.5, -9);
        drive.driveStraightInches(0.5, -27); // +8 in added to 72.0 in so it reaches t
        sleep(150);


        if (!opModeIsActive()) return;

        // 4. Shoot from the new spot
        shooter.waitUntilAtSpeed(3.0); // spin up shooter for second shot
        intake.setPower(0.5); // feed collected balls into shooter
        shooter.gateForSeconds(2.0); // open gate for 2.0s to shoot
        sleep(150);
        intake.stop();
        shooter.shooterStop();
        if (!opModeIsActive()) return;

        // 5. Park and stop all mechanisms
        drive.driveStraightInches(0.5, 27); // +8 in added to 72.0 in
        drive.strafeInches(0.5, 50); // TODO: tune parking strafe distance
        drive.driveStraightInches(0.5, 0.0); // +8 in added to 72.0 in so it reaches the other side
        sleep(150);

        shooter.shooterStop();
        intake.stop();
        drive.stop();
    }
}