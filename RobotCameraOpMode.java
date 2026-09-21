// RobotCameraOpMode.java - Main FTC OpMode combining Vision & Base Movement
// This file acts as your main program loop where you manage state and read vision results.

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import java.util.List;

@Autonomous(name="Basic Vision OpMode Starter", group="Robot")
public class RobotCameraOpMode extends LinearOpMode {

    // Define basic drivetrain motors
    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;

    // Instantiate your vision helper files
    private AprilTagLocator aprilTagHelper = new AprilTagLocator();
    // (Optional) Instantiate your blob detection pipeline here if not using the default portal

    @Override
    public void runOpMode() {
        // 1. Initialize robot hardware drivetrain motors
        leftDrive  = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);

        // 2. Initialize camera components
        telemetry.addData("Status", "Initializing Vision System...");
        telemetry.update();
        
        aprilTagHelper.initAprilTag(hardwareMap);

        telemetry.addData("Status", "Initialized. Waiting for Start...");
        telemetry.update();

        waitForStart();

        // 3. Main runtime loop
        while (opModeIsActive()) {
            // Get active detections from the AprilTag processor
            List<AprilTagDetection> currentDetections = aprilTagHelper.getDetections();
            
            if (currentDetections != null && currentDetections.size() > 0) {
                for (AprilTagDetection detection : currentDetections) {
                    if (detection.metadata != null) {
                        telemetry.addData("Detected Tag ID", detection.id);
                        telemetry.addData("Distance to Tag (X)", detection.ftcPose.x);
                        telemetry.addData("Distance to Tag (Y)", detection.ftcPose.y);
                        
                        // Basic Action: If too far away from target ID 1, drive forward slightly
                        if (detection.id == 1 && detection.ftcPose.y > 12.0) {
                            leftDrive.setPower(0.3);
                            rightDrive.setPower(0.3);
                        } else {
                            leftDrive.setPower(0.0);
                            rightDrive.setPower(0.0);
                        }
                    }
                }
            } else {
                telemetry.addData("Vision Status", "No AprilTags detected. Scanning...");
                leftDrive.setPower(0.0);
                rightDrive.setPower(0.0);
            }

            telemetry.update();
            sleep(20); // Small pause to prevent CPU locking
        }
    }
}
