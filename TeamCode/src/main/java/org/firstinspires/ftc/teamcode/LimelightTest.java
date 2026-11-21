package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "Limelight Test")
public class LimelightTest extends LinearOpMode {

    private Limelight limelight;
    // Set to true ONLY when the robot is on blocks/floor and safe to spin wheels
    private static final boolean TEST_MOVEMENT = false;

    @Override
    public void runOpMode() {
        // 1. Initialize your Limelight helper class
        limelight = new Limelight(this);

        // Make sure "limelight" matches your Robot Configuration name
        // and 0 matches your AprilTag pipeline slot
        limelight.hardwareMapLimelight("limelight", 0);

        // Optional: Initialize Movement only if testing alignment motors
        Movement movement = null;
        if (TEST_MOVEMENT) {
            movement = new Movement(limelight.opMode); // Adjust if your Movement constructor differs
        }

        telemetry.addData("Status", "Limelight Initialized. Ready to start.");
        telemetry.update();

        waitForStart();

        // 2. Continuously read and display AprilTag data while running
        while (opModeIsActive()) {
            // Calls your addTelemetry() method (shows Valid, tx, ty, ta)
            limelight.addTelemetry();

            // Example of testing individual getter methods
            if (limelight.isTargetValid()) {
                double tx = limelight.getTx();
                telemetry.addData("Alignment Direction", tx > 0 ? "Turn Right" : "Turn Left");

                // Optional: Run alignToTarget once if movement testing is enabled
                if (TEST_MOVEMENT && movement != null) {
                    boolean aligned = limelight.alignToTarget(movement);
                    telemetry.addData("Aligned?", aligned);
                    telemetry.update();
                    break; // Stop after testing alignment once
                }
            }

            telemetry.update();
        }
    }
}
