package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Intake {
    private DcMotor intakeMotor;

    // Switch all these values to ones on your intake motor
    private final double TICKS_PER_REV = 537.7;     // Adjust for your specific motor (e.g., GoBILDA 19.2:1)
    private final double GEAR_RATIO = 1.0;          // Gear ratio of the intake mechanism if applicable

    private LinearOpMode opMode;
    private ElapsedTime runtime = new ElapsedTime();

    public Intake(LinearOpMode opMode) {
        this.opMode = opMode;
    }

    public void hardwareMapMotor(String motorName) {
        intakeMotor = opMode.hardwareMap.get(DcMotor.class, motorName);
    }

    /**
     * Set continuous power to the intake.
     * @param power positive power typically pulls game elements in, negative ejects them.
     */
    public void setPower(double power) {
        intakeMotor.setPower(power);
    }

    public void stop() {
        intakeMotor.setPower(0);
    }

    /**
     * Run the intake for a specified number of seconds.
     */
    public void runForSeconds(double power, double seconds) {
        runtime.reset();
        while (opMode.opModeIsActive() && runtime.seconds() < seconds) {
            setPower(power);
            opMode.telemetry.addData("Intake running for:", "%.2f seconds", runtime.seconds());
            opMode.opModeIsActive(); // Keep connection alive
            opMode.telemetry.update();
        }
        stop();
    }

    public void setEncoderMode(String mode) {
        switch (mode.toUpperCase()) {
            case "RUN_TO_POSITION":
                intakeMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                break;
            case "RUN_USING_ENCODER":
                intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                break;
            case "STOP_AND_RESET_ENCODER":
                intakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                break;
            case "RUN_WITHOUT_ENCODER":
                intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                break;
            default:
                opMode.telemetry.addData("NOT A PARAMETER", 0);
                opMode.telemetry.update();
                break;
        }
    }

    /**
     * Run the intake for a specific number of encoder ticks.
     */
    public void runTicks(int ticks, double power) {
        setEncoderMode("STOP_AND_RESET_ENCODER");

        intakeMotor.setTargetPosition(ticks);
        setEncoderMode("RUN_TO_POSITION");

        intakeMotor.setPower(Math.abs(power));

        while (opMode.opModeIsActive() && intakeMotor.isBusy()) {
            opMode.telemetry.addData("Running intake to target ticks", ticks);
            opMode.telemetry.addData("Current Ticks", intakeMotor.getCurrentPosition());
            opMode.telemetry.update();
        }

        stop();
        setEncoderMode("RUN_USING_ENCODER");
    }

    /**
     * Rotate the intake a specific number of degrees (useful for precise indexing or arm-like intakes).
     */
    public void rotateDegrees(double power, double degrees) {
        if (opMode.opModeIsActive()) {
            int targetTicks = (int) (Math.round((degrees / 360.0) * TICKS_PER_REV * GEAR_RATIO));

            setEncoderMode("STOP_AND_RESET_ENCODER");
            intakeMotor.setTargetPosition(targetTicks);
            setEncoderMode("RUN_TO_POSITION");

            intakeMotor.setPower(Math.abs(power));

            while (opMode.opModeIsActive() && intakeMotor.isBusy()) {
                opMode.telemetry.addData("Target Degrees", degrees);
                opMode.telemetry.addData("Current Position", intakeMotor.getCurrentPosition());
                opMode.telemetry.addData("Target Ticks", targetTicks);
                opMode.telemetry.update();
            }

            stop();
            setEncoderMode("RUN_USING_ENCODER");
        }
    }
}