// AprilTagDetection.java - Basic AprilTag Locator Pipeline for FTC
// This file initializes the built-in SDK AprilTag processor to read tag details.
// Simple team code we will change once we get the cmaera. 




package org.firstinspires.ftc.Circuit.Overload; //might need to use teamcode here instead.

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
        // Create the AprilTag processor with default settings
        aprilTagProcessor = AprilTagProcessor.createEasyDeltaToRobot();

        // Create the vision portal containing our processor
        visionPortal = VisionPortal.easyCreateWithDefaults(
                hardwareMap.get(WebcamName.class, "Webcam 1"), aprilTagProcessor);
    }

    // Call this inside your loop to scan for active targets
    public List<AprilTagDetection> getDetections() {
        if (aprilTagProcessor != null) {
            return aprilTagProcessor.getDetections();
        }
        return null;
    }
}



//infact this seems not to hard to build, hard part will be to code the robot to do things with the april tag code.
