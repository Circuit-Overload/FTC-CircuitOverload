package org.firstinspires.ftc.teamcode.FlywheelAdvanced;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;



public class kPTuner extends OpMode {
    Flywheel flywheel = new Flywheel();


    // put kV from tuning using the kV Tuner class
    public static double kV = 0;


    // put kS from tuning using the kS Tuner class
    public double kS;

    public double kP = 0;


    // Highest point that we wanted to launch a ball at from our flywheel away
    public double goalRPM;

    double[] increments = {0.000001, 0.00001, 0.0001, 0.001, 0.01};

    int incrementIdx = 4;

    @Override
    public void init() {
        flywheel.init(hardwareMap);
    }

    @Override
    public void loop() {

        if (gamepad1.dpadRightWasPressed() && incrementIdx < 4) {
            incrementIdx++;
        } else if (gamepad1.dpadLeftWasPressed() && incrementIdx > 0) {
            incrementIdx--;
        }

        if (gamepad1.a) {
            goalRPM = /* put value of goal rpm here */ 0;
        }

        double currentStep = increments[incrementIdx];

        // Change kV

        if (gamepad1.dpadUpWasPressed()) {
            kP += currentStep;
        }
        if (gamepad1.dpadDownWasPressed()) {
            kP -= currentStep;
        }


        double feedForward = (kV * goalRPM) + kS;
        double error = goalRPM - flywheel.getRPM();
        double feedBack =  error * kP;
        flywheel.setMotorPower(feedBack + feedForward);


        telemetry.addData("Step: ", currentStep);
        telemetry.addData("kV", "%.6f", kV);
        telemetry.addData("Error", error);
        telemetry.addData("RPM", flywheel.getRPM());
        telemetry.addData("Ticks Per Sec", flywheel.getTicksPerSec());
    }

}