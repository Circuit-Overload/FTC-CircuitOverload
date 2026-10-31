package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Limelight {
    private Limelight3A limelight;
    private final LinearOpMode opMode;

    public Limelight(LinearOpMode opMode) {
        this.opMode = opMode;
    }

    /**
     * Initializes and starts the Limelight sensor on the specified pipeline.
     */
    public void hardwareMapLimelight(String limelightName, int pipelineIndex) {
        limelight = opMode.hardwareMap.get(Limelight3A.class, limelightName);

        limelight.setPollRateHz(100);
        limelight.start();
        limelight.pipelineSwitch(pipelineIndex);
    }

    /**
     * Returns the raw result object from the Limelight.
     */
    public LLResult getResult() {
        if (limelight != null) {
            return limelight.getLatestResult();
        }
        return null;
    }

    /**
     * Checks if a valid target is currently detected by the Limelight.
     */
    public boolean isTargetValid() {
        LLResult result = getResult();
        return result != null && result.isValid();
    }

    /**
     * Returns the horizontal offset (tx) from the crosshair in degrees.
     */
    public double getTx() {
        LLResult result = getResult();
        if (result != null && result.isValid()) {
            return result.getTx();
        }
        return 0.0;
    }

    /**
     * Returns the vertical offset (ty) from the crosshair in degrees.
     */
    public double getTy() {
        LLResult result = getResult();
        if (result != null && result.isValid()) {
            return result.getTy();
        }
        return 0.0;
    }

    /**
     * Returns the target area (ta) percentage (0.0 to 100.0).
     */
    public double getTa() {
        LLResult result = getResult();
        if (result != null && result.isValid()) {
            return result.getTa();
        }
        return 0.0;
    }

    /**
     * Adds Limelight diagnostic telemetry to the driver hub.
     */
    public void addTelemetry() {
        boolean valid = isTargetValid();
        opMode.telemetry.addData("Limelight Target Found", valid);
        if (valid) {
            opMode.telemetry.addData("Horizontal Offset (tx)", getTx());
            opMode.telemetry.addData("Vertical Offset (ty)", getTy());
            opMode.telemetry.addData("Target Area (ta)", getTa());
        }
    }

    public boolean alignToTarget(Movement movement) {
        ElapsedTime timer = new ElapsedTime();

        timer.reset();

        while (opMode.opModeIsActive()
                && timer.seconds() < 1.5) {

            LLResult result = getResult();

            // Stop if the target is lost.

            if (result == null || !result.isValid()) {

                movement.stop();

                return false;

            }

            double tx = result.getTx();

            // Target is centered.

            if (Math.abs(tx) <= 2.0) {

                movement.stop();

                return true;

            }

            // Slow corrective turn.

            double power = 0.15;

            if (tx > 0) {

                movement.turn(power);

            } else {

                movement.turn(-power);

            }

            opMode.idle();

        }

        movement.stop();

        return false;

    }
}

