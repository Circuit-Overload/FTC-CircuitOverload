package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import java.util.List;

    public class AprilTagLocator {
        public AprilTagProcessor aprilTagProcessor;
        public VisionPortal visionPortal;

        // Initialize the AprilTag system using the modern FTC VisionPortal API
        public void initAprilTag(HardwareMap hardwareMap) {
            // Create the AprilTag processor using SDK defaults
            aprilTagProcessor = AprilTagProcessor.easyCreateWithDefaults();

            // Create the vision portal containing our processor and webcam
            visionPortal = VisionPortal.easyCreateWithDefaults(
                    hardwareMap.get(WebcamName.class, "Webcam 1"),
                    aprilTagProcessor
            );
        }

        // Call this inside your loop to scan for active targets
        public List<AprilTagDetection> getDetections() {
            if (aprilTagProcessor != null) {
                return aprilTagProcessor.getDetections();
            }
            return null;
        }

        // Clean up resources when the OpMode finishes
        public void closePortal() {
            if (visionPortal != null) {
                visionPortal.close();
            }
        }
    }

