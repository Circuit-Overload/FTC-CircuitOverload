package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Shooter {
    private DcMotorEx motor_shooter;

    private Servo servo_shooter;

    // Servo angles (degrees). Set SERVO_RANGE_DEGREES to your servo's full range
    // (180 for standard servos, 300 for goBILDA torque/speed servos).
    private final double SERVO_RANGE_DEGREES = 300.0;
    private final double SERVO_ON_DEGREES = 50.0;
    private final double SERVO_OFF_DEGREES = 5.0;

    // Shooter closed-loop velocity control constants
    // goBILDA Yellow Jacket encoder: 28 ticks per motor revolution
    private final double SHOOTER_TARGET_RPM = 2760.0;
    private final double SHOOTER_TICKS_PER_REV = 28.0;
    private final double SHOOTER_TARGET_TPS = (SHOOTER_TARGET_RPM * SHOOTER_TICKS_PER_REV) / 60.0; // ~1540 TPS
    private final double SHOOTER_DIRECTION = 1.0;

    // Shooter velocity PIDF gains
    private final double SHOOTER_P = 32.0;
    private final double SHOOTER_I = 0.0;
    private final double SHOOTER_D = 2.5;
    private final double SHOOTER_F = 15.0;
    private final double SHOOTER_AT_SPEED_TOLERANCE_RPM = 150.0;

    private final LinearOpMode opMode;
    private final ElapsedTime runtime = new ElapsedTime();

    public Shooter(LinearOpMode opMode) {
        this.opMode = opMode;
    }

    public void hardwareMapShooters(String motorName) {
        motor_shooter = opMode.hardwareMap.get(DcMotorEx.class, motorName);

        // Use the built-in encoder so the hub regulates velocity (PIDF)
        motor_shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor_shooter.setVelocityPIDFCoefficients(SHOOTER_P, SHOOTER_I, SHOOTER_D, SHOOTER_F);
    }

    public void hardwareMapServo(String servoName) {
        // Maps the servo using the exact name configured in your Driver Hub
        servo_shooter = opMode.hardwareMap.get(Servo.class, servoName);
        servoOn(); // default position
    }

    public void servoOn() {
        servo_shooter.setPosition(SERVO_ON_DEGREES / SERVO_RANGE_DEGREES);
    }

    public void servoOff() {
        servo_shooter.setPosition(SERVO_OFF_DEGREES / SERVO_RANGE_DEGREES);
    }

    public void servoSetPosition(double position) {
        servo_shooter.setPosition(position);
    }

    // Optional: Reverse the servo direction if it moves the wrong way
    public void servoSetDirection(Servo.Direction direction) {
        servo_shooter.setDirection(direction);
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            motor_shooter.setVelocity(SHOOTER_DIRECTION * SHOOTER_TARGET_TPS);
        } else {
            motor_shooter.setVelocity(0.0);
        }
    }

    public void stop() {
        motor_shooter.setVelocity(0.0);
    }

    public double getActualRpm() {
        double velocityTps = motor_shooter.getVelocity();
        return Math.abs(velocityTps) * 60.0 / SHOOTER_TICKS_PER_REV;
    }

    public boolean isAtSpeed() {
        return Math.abs(getActualRpm() - SHOOTER_TARGET_RPM) <= SHOOTER_AT_SPEED_TOLERANCE_RPM;
    }

    /**
     * Blocks execution in autonomous until the shooter reaches target speed or times out.
     */
    public boolean waitUntilAtSpeed(double timeoutSeconds) {
        runtime.reset();
        setEnabled(true);

        while (opMode.opModeIsActive() && !isAtSpeed() && runtime.seconds() < timeoutSeconds) {
            opMode.telemetry.addData("Shooter Status", "Spinning up... Actual RPM: %.1f", getActualRpm());
            opMode.telemetry.update();
        }

        return isAtSpeed();
    }

    /**
     * Spins up, waits until up to speed, runs for specified seconds, then shuts down.
     */
    public void fireForSeconds(double durationSeconds, double timeoutSeconds) {
        boolean ready = waitUntilAtSpeed(timeoutSeconds);

        if (!ready) {
            opMode.telemetry.addData("Shooter Warning", "Timed out before reaching target RPM!");
            opMode.telemetry.update();
        }

        runtime.reset();
        while (opMode.opModeIsActive() && runtime.seconds() < durationSeconds) {
            opMode.telemetry.addData("Shooter Status", "Firing! Time left: %.2f", durationSeconds - runtime.seconds());
            opMode.telemetry.update();
        }

        stop();
    }
    public void start(double power) {
        motor_shooter.setPower(power);
    }
    public void servoForSeconds (double durationSeconds) {
        runtime.reset();
        servoOff();
        while (opMode.opModeIsActive() && runtime.seconds() < durationSeconds) {
            opMode.telemetry.addData("Servo Status", "Off! Time left: %.2f", durationSeconds - runtime.seconds());
            opMode.telemetry.update();
        }
        servoOn();
    }

}


// Inside your OpMode's runOpMode():
//Limelight vision = new Limelight(this);
//vision.hardwareMapLimelight("limelight", 0); // "limelight" is the config name, 0 is your pipeline
//
// Inside your loop:
//vision.addTelemetry();
//if (vision.isTargetValid()) {
//double tx = vision.getTx();
// Do your auto-aiming or tracking logic here!
//}
