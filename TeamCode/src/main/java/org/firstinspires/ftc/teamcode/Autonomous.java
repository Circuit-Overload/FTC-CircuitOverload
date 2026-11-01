package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous
public class Autonomous extends LinearOpMode {

    Movement drive;
    Intake intake;
    Shooter shooter;
    Limelight vision;

    @Override
    public void runOpMode() {
        // 1. Initialize all mechanism classes
        drive = new Movement(this);
        intake = new Intake(this);
        shooter = new Shooter(this);
        vision = new Limelight(this);

        // 2. Map hardware
        drive.hardwareMapMotors("motor_front_left", "motor_back_left", "motor_front_right", "motor_back_right");
        intake.hardwareMapMotor("motor_intake");
        shooter.hardwareMapShooters("motor_shooter");
        vision.hardwareMapLimelight("limelight", 0); // Pipeline 0 for target tracking

        telemetry.addLine("Initialization Complete. Waiting for start.");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            // Path1();
            // Path2();
            // Path3();
            // Path4();
            SketchPath();

            // uncomment the path that you need to test above
        }

    }
    /**
     * Pathway final: start on tile W1, strafe left to the 2nd spot (V1),
     * drive diagonally up to the pick area, then strafe left to park.
     * Assumes 1 tile = 24 inches and the robot faces up the field.
     */
    public void SketchPath() {
        drive.strafeInches(0.6, -24);

        double diagonalInches = Math.hypot(36, 72);
        double diagonalAngle = Math.toDegrees(Math.atan2(36, 72));
        drive.driveDiagonalInches(0.6, diagonalAngle, diagonalInches);

        drive.strafeInches(0.6, -36);
    }

    public void Path1 () {
        // ==========================================
        // INITIAL SCORE PHASE (Runs exactly once)
        // ==========================================
        drive.strafeInches(0.7, -24);
        drive.driveStraightInches(0.7, 24);

        boolean aligned = vision.alignToTarget(drive);
        if (aligned) {
            shooter.fireForSeconds(1.5, 2.0);
        } else {
            telemetry.addLine("Vision align failed! Skipping shot.");
            telemetry.update();
        }

        // ==========================================
        // REPEATING CYCLES (Runs 3 times)
        // ==========================================
        for (int i = 0; i < 3; i++) {

            // Safety catch: Break out of the loop if Auto is almost over (e.g., > 25 seconds)
            if (getRuntime() > 25.0) {
                break;
            }

            // --- INTAKE PHASE ---
            drive.driveStraightInches(0.7, -24);
            drive.strafeInches(0.7, 36);
            drive.driveStraightInches(0.4, 2);
            intake.runForSeconds(0.8, 1.5);

            // --- SCORING PHASE ---
            drive.driveStraightInches(0.5, -2);
            drive.strafeInches(0.7, -36);
            drive.driveStraightInches(0.7, 24);

            if (vision.alignToTarget(drive)) {
                shooter.fireForSeconds(1.5, 2.0);
            }
        }

        // ==========================================
        // FINAL PARK SEQUENCE (Runs exactly once)
        // ==========================================
        drive.driveStraightInches(0.8, -24);
        drive.strafeInches(0.8, 48);
    }

    public void Path2 () {
        // ==========================================
        // INITIAL SCORE PHASE
        // ==========================================
        // Start at (36, -60). Strafe left to X = 12
        drive.strafeInches(0.6, -24);
        // Drive forward to Scoring Junction Y = -36
        drive.driveStraightInches(0.6, 24);

        // Wait 0.5s for stability as depicted in the Path 2 diagram
        sleep(500);

        if (vision.alignToTarget(drive)) {
            shooter.fireForSeconds(1.5, 2.0);
        } else {
            telemetry.addLine("Vision align failed! Skipping shot.");
            telemetry.update();
        }

        // ==========================================
        // CYCLE 1: INTAKE PHASE
        // ==========================================
        // Back up to Y = -60
        drive.driveStraightInches(0.6, -24);

        // Strafe right to Intake Zone X = 48
        drive.strafeInches(0.6, 36);

        // Drive forward slightly into the depot Y = -58
        drive.driveStraightInches(0.4, 2);

        intake.runForSeconds(0.8, 1.5);

        // ==========================================
        // CYCLE 1: SCORING PHASE
        // ==========================================
        // Back out of depot to Y = -60
        drive.driveStraightInches(0.5, -2);

        // Strafe left back to X = 12
        drive.strafeInches(0.6, -36);

        // Drive forward to Scoring Junction Y = -36
        drive.driveStraightInches(0.6, 24);

        if (vision.alignToTarget(drive)) {
            shooter.fireForSeconds(1.5, 2.0);
        }

        // ==========================================
        // FINAL PARK SEQUENCE (DEFENSIVE OFFSET)
        // ==========================================
        // Back out to Y = -60
        drive.driveStraightInches(0.7, -24);

        // Strafe right all the way to Defensive Offset Park (70, -60)
        // 70 (Target X) - 12 (Current X) = 58 inches right
        drive.strafeInches(0.8, 58);
    }
    public void Path3 () {
        // ==========================================
        // INITIAL SCORE PHASE
        // ==========================================
        // Start at (36, -60). Strafe left to X = 12
        drive.strafeInches(0.6, -24);
        // Drive forward to Scoring Junction Y = -36
        drive.driveStraightInches(0.6, 24);

        if (vision.alignToTarget(drive)) {
            shooter.fireForSeconds(1.5, 2.0);
        } else {
            telemetry.addLine("Vision align failed! Skipping shot.");
            telemetry.update();
        }

        // ==========================================
        // MID-FIELD DOMINANCE (TACTICAL INTERFERENCE)
        // ==========================================
        // Current Position: (12, -36). Target: (24, -24)
        // Strafe right to X = 24 (24 - 12 = 12 inches)
        drive.strafeInches(0.6, 12);
        // Drive forward to Y = -24 (-24 - (-36) = 12 inches)
        drive.driveStraightInches(0.6, 12);

        // Pause & Scan (Wait 0.4s) to allow for opponent observation and tactical delay
        sleep(400);

        // ==========================================
        // CYCLE 2: INTAKE PHASE
        // ==========================================
        // Current Position: (24, -24). Target: Intake Zone at (48, -58)
        // Back up to Y = -58 (-58 - (-24) = -34 inches)
        drive.driveStraightInches(0.6, -34);

        // Strafe right to Intake Zone X = 48 (48 - 24 = 24 inches)
        drive.strafeInches(0.6, 24);

        // Activate Rollers
        intake.runForSeconds(0.8, 1.5);

        // ==========================================
        // CYCLE 2: SCORING PHASE
        // ==========================================
        // Back out of depot to Y = -60
        drive.driveStraightInches(0.5, -2);

        // Strafe left back to X = 12
        drive.strafeInches(0.6, -36);

        // Drive forward to Scoring Junction Y = -36
        drive.driveStraightInches(0.6, 24);

        if (vision.alignToTarget(drive)) {
            shooter.fireForSeconds(1.5, 2.0);
        }

        // ==========================================
        // FINAL PARK SEQUENCE (DEFENSIVE OFFSET)
        // ==========================================
        // Back out to Y = -60
        drive.driveStraightInches(0.7, -24);

        // Strafe right all the way to Defensive Offset Park (70, -60)
        // 70 (Target X) - 12 (Current X) = 58 inches right
        drive.strafeInches(0.8, 58);
    }

    public void Path4 () {
        // ==========================================
        // INITIAL SCORE PHASE
        // ==========================================
        // Start at (36, -60). Strafe left to X = 12
        drive.strafeInches(0.6, -24);

        // Wait 0.3s for chassis to settle (as specified in diagram)
        sleep(300);

        // Drive forward to Scoring Junction Y = -36
        drive.driveStraightInches(0.6, 24);

        // Blind/Low-Risk Approach: Shoot whether vision aligns or not
        if (vision.alignToTarget(drive)) {
            shooter.fireForSeconds(1.5, 2.0);
        } else {
            // Still shoot even if alignment fails, prioritizing the cycle
            shooter.fireForSeconds(1.5, 2.0);
        }

        // ==========================================
        // CYCLE 1: INTAKE PHASE
        // ==========================================
        // Back up strictly on the Y-axis to (12, -60)
        drive.driveStraightInches(0.6, -24);

        // Strafe right to Intake Zone X = 48
        drive.strafeInches(0.6, 36);

        // Drive forward slightly into the depot Y = -58
        drive.driveStraightInches(0.4, 2);

        intake.runForSeconds(0.8, 1.5);

        // ==========================================
        // CYCLE 1: SCORING PHASE
        // ==========================================
        // Back out of depot to Y = -60
        drive.driveStraightInches(0.5, -2);

        // Strafe left back to corner X = 12
        drive.strafeInches(0.6, -36);

        // Wait 0.3s for chassis to settle before final approach
        sleep(300);

        // Drive forward to Scoring Junction Y = -36
        drive.driveStraightInches(0.6, 24);

        if (vision.alignToTarget(drive)) {
            shooter.fireForSeconds(1.5, 2.0);
        } else {
            shooter.fireForSeconds(1.5, 2.0);
        }

        // ==========================================
        // FINAL PARK SEQUENCE
        // ==========================================
        // Back out to Y = -60
        drive.driveStraightInches(0.7, -24);

        // Strafe right to Park Zone (60, -60)
        // 60 (Target X) - 12 (Current X) = 48 inches right
        drive.strafeInches(0.8, 48);
    }

    // PLACE ROBOT ALWAYS AT (36, -60) COORDINATE.

    }
