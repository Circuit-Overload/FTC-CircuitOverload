package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "Limelight Test")
public class LimelightTest extends LinearOpMode {

    private Limelight limelight;
    private Shooter shooter;

    // Set to true if the Limelight is mounted ON the turret; false if mounted on the chassis
    private static final boolean CAMERA_ON_TURRET = true;

    @Override
    public void runOpMode() {
        limelight = new Limelight(this);
        shooter = new Shooter(this);

        // 1. Hardware map BOTH the Limelight and the turret servo
        limelight.hardwareMapLimelight("limelight", 0);
        shooter.hardwareMapTurret("turret_servo"); // Match your Robot Config name!

        telemetry.addData("Status", "Initialized. Turret centered.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            limelight.addTelemetry();

            if (limelight.isTargetValid()) {
                double tx = limelight.getTx();

                if (CAMERA_ON_TURRET) {
                    // Camera moves with turret: nudge turret slightly each loop until tx == 0
                    if (Math.abs(tx) > 1.5) {
                        shooter.adjustTurretAngleDegrees(tx * 0.15); // Flip sign (-tx) if it turns away!
                    }
                } else {
                    // Camera is fixed on chassis: point directly to (Center + tx)
                    shooter.setTurretAngleDegrees(150.0 + tx); // Flip to (150.0 - tx) if reversed!
                }
            }

            telemetry.addData("Turret Angle (deg)", shooter.getTurretAngleDegrees());
            telemetry.update();
        }
    }
}
