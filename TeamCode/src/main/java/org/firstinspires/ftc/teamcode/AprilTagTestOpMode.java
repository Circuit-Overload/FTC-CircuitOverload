package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import java.util.List;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

@TeleOp(name = "AprilTag Test OpMode", group = "Sensor")
public class AprilTagTestOpMode extends LinearOpMode {

    private AprilTagLocator tagLocator;

    @Override
    public void runOpMode() {
        // Instantiate and initialize our locator helper
        tagLocator = new AprilTagLocator();
        tagLocator.initAprilTag(hardwareMap);

        telemetry.addData("Status", "Initialized. Waiting for start...");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            List<AprilTagDetection> currentDetections = tagLocator.getDetections();

            telemetry.addData("# AprilTags Detected", currentDetections != null ? currentDetections.size() : 0);

            if (currentDetections != null) {
                for (AprilTagDetection detection : currentDetections) {

                    // 1. Check if it's a single tag (so we can safely read ID and metadata)
                    if (detection instanceof AprilTagSingleDetection) {
                        AprilTagSingleDetection singleDet = (AprilTagSingleDetection) detection;

                        if (singleDet.metadata != null) {
                            // Print known tag info using singleDet
                            telemetry.addLine(String.format("\n== Tag ID %d (%s)", singleDet.id, singleDet.metadata.name));
                            telemetry.addLine(String.format("XYZ (in): %.1f, %.1f, %.1f", singleDet.ftcPose.x, singleDet.ftcPose.y, singleDet.ftcPose.z));
                            telemetry.addLine(String.format("Range/Bearing: %.1f in, %.1f°", singleDet.ftcPose.range, singleDet.ftcPose.bearing));
                        } else {
                            // Print fallback for unknown tags
                            telemetry.addLine(String.format("\n== Tag ID %d (Unknown Tag)", singleDet.id));
                            telemetry.addLine(String.format("Center (pixels): %.0f, %.0f", singleDet.center.x, singleDet.center.y));
                        }
                    }
                }
            }

            telemetry.update();
            sleep(20); // Prevent spamming the CPU
        }

        // Close camera feed cleanly on exit
        tagLocator.closePortal();
    }
}