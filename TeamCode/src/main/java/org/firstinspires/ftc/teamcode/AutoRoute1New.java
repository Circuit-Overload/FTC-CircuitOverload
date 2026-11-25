package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous
public class AutoRoute1New extends LinearOpMode {

    Movement drive;
    Intake intake;
    Shooter shooter;

    // TODO: Tune these values for the flower on the opposite side of your starting side
    private static final double DRIVE_POWER = 0.5;
    private static final double TURN_POWER = 0.5;
    private static final double TURN_TIMEOUT_S = 3.0;
    private static final double INTAKE_POWER = 1.0;

    private static final double TURN_TO_OPPOSITE_FLOWER_DEG = 90.0; // TODO (+ clockwise, - counter-clockwise per turnDegreesIMU)
    private static final double CROSS_TO_FLOWER_STRAIGHT_IN = 48.0; // TODO: distance across field toward opposite-side flower
    private static final double CROSS_TO_FLOWER_STRAFE_IN = 0.0;   // TODO (+ right, - left)
    private static final double FLOWER_SWEEP_IN = 12.0;            // TODO: forward push into flower while intaking

    private static final double SHOOTER_SPINUP_TIMEOUT_S = 3.0;
    private static final double GATE_OPEN_DURATION_S = 2.0;

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
        shooter.waitUntilAtSpeed(3.0);
        intake.setPower(1);
        shooter.gateForSeconds(2.0);
        drive.driveStraightInches(0.5, -5);
        sleep(200);
        drive.turnDegreesIMU(0.5, 90, 3.0);
        sleep(200);
        drive.strafeInches(0.5, -8);
        drive.strafeInches(0.1, 0.5);
        //drive.turnDegreesIMU(0.5, -5, 3.0 );
        drive.driveStraightInches(0.5, 48);
        drive.strafeInches(0.5, 2);
        sleep(200);
        drive.driveStraightInches(0.5, -20);
        sleep(200);
        drive.strafeInches(0.5, 24);
        sleep(200);
        drive.turnDegreesIMU(0.5, 90, 3.0);
        sleep(200);
        drive.driveStraightInches(0.5, 72);
        sleep(200);
        drive.strafeInches(0.5, 24);
        sleep(200);
        drive.driveStraightInches(0.5, 26);
        sleep(200);
        shooter.gateForSeconds(2.0);
        //NEW CODE
        drive.driveStraightInches(0.5, 3);
        drive.turnDegreesIMU(0.5, 90, 3.0);
        drive.strafeInches(0.5, -2);

        for (int i = 0 ; i < 3 ; i++) {
            drive.driveStraightInches(0.25, 5);
            drive.driveStraightInches(0.25, -5);
        }
        // NEW CODE
        intake.setPower(1);
        drive.strafeInches(0.5, -6);

        drive.driveStraightInches(0.5, 30);
        drive.driveStraightInches(0.5, -1);
        drive.strafeInches(0.5, -6);
        // drive.strafeInches(0.5, -48);
        shooter.shooterStop();
        intake.stop();
    }

}