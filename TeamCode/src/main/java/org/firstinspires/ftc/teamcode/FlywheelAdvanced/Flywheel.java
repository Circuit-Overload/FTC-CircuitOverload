package org.firstinspires.ftc.teamcode.FlywheelAdvanced;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Flywheel {

    private DcMotorEx m1;

    private double encoderCPM = 28.0;

    private double kV /*2 digits*/, kS /*0.6 digits*/, kP;



    public void init(HardwareMap hwMap) {
        m1 = hwMap.get(DcMotorEx.class, "motor_flywheel");
        m1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setMotorPower(double power) {
        m1.setPower(power);
    }


    public double getTicksPerSec() {
        return m1.getVelocity();
    }

    public double getRPM () {
        return (getTicksPerSec()/encoderCPM) * 60;
    }
}


