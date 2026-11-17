

package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;

import java.util.List;

public class Turret {
    private DcMotor motor_turret;
    private Limelight3A limelight;
    private final LinearOpMode opMode;

    public Turret(LinearOpMode opMode) {
        this.opMode = opMode;
    }

    public void hardwareMapTurret(String motorName, String limelightName, int pipelineIndex) {
        motor_turret = opMode.hardwareMap.get(DcMotor.class, motorName);
        motor_turret.setDirection(DcMotor.Direction.FORWARD);
        motor_turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        limelight = opMode.hardwareMap.get(Limelight3A.class, limelightName);
        limelight.setPollRateHz(100);
        limelight.start();
        limelight.pipelineSwitch(pipelineIndex);
    }

    public void setPower(double power) {
        if (motor_turret != null) {
            motor_turret.setPower(power);
        }
    }

    public void stop() {
        setPower(0.0);
    }

    public boolean isTargetValid() {
        if (limelight == null) return false;
        LLResult result = limelight.getLatestResult();
        return result != null && result.isValid();
    }

    public double getTx() {
        if (limelight == null) return 0.0;
        LLResult result = limelight.getLatestResult();
        return (result != null && result.isValid()) ? result.getTx() : 0.0;
    }

    public long getFiducialID() {
        if (limelight == null) return -1;
        LLResult result = limelight.getLatestResult();

        if (result != null && result.isValid()) {
            List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
            if (fiducials != null && !fiducials.isEmpty()) {
                // Returns the ID of the first detected AprilTag/fiducial
                return fiducials.get(0).getFiducialId();
            }
        }
        return -1;
    }
}

