package org.firstinspires.ftc.teamcode;

import static java.lang.Math.abs;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="TeleOp_Main", group="Linear OpMode")
public class Proto1code extends LinearOpMode {

    // Hardware Declarations
    private DcMotor motor_front_left, motor_front_right, motor_back_left, motor_back_right;
    private DcMotor motor_intake;
    private DcMotor motor_shooter;
    private Servo servo_intake;

    // Toggle Tracking Variables
    private boolean intakeOn = false;
    private boolean lastAState = false;

    private boolean shooterOn = false;
    private boolean lastXState = false;

    // Servo Angle Scaling (40 degrees out of a 300-degree max range)
    private static final double SERVO_MIN_POS = 50.0 / 300.0 ;
    private static final double SERVO_MAX_POS = -70.0/300.0;

    @Override
    public void runOpMode() {

        // Hardware Mapping
        motor_front_left = hardwareMap.get(DcMotor.class, "motor_front_left");
        motor_back_left = hardwareMap.get(DcMotor.class, "motor_back_left");
        motor_front_right = hardwareMap.get(DcMotor.class, "motor_front_right");
        motor_back_right = hardwareMap.get(DcMotor.class, "motor_back_right");

        motor_intake = hardwareMap.get(DcMotor.class, "motor_intake");
        motor_shooter = hardwareMap.get(DcMotor.class, "motor_shooter");
        servo_intake = hardwareMap.get(Servo.class, "servo_intake");

        // Reverse left side motors so positive power drives forward
        motor_front_left.setDirection(DcMotor.Direction.REVERSE);
        motor_back_left.setDirection(DcMotor.Direction.REVERSE);
        motor_front_right.setDirection(DcMotor.Direction.FORWARD);
        motor_back_right.setDirection(DcMotor.Direction.FORWARD);

        waitForStart();

        while (opModeIsActive()) {

            // -------------------------------------------------------------
            // 1. STRAFE / DRIVE LOGIC (Left Joystick Only)
            // -------------------------------------------------------------
            // Note: Gamepad Y-axis is inverted in FTC (-1 is UP, +1 is DOWN)
            double left_y = -gamepad1.left_stick_y;
            double left_x = gamepad1.left_stick_x;
            double right_x = gamepad1.right_stick_x;

            // Add deadzone to robot
            if (abs(left_y) <= 0.05) {
                left_y = 0;
            }

            if (abs(left_x) <= 0.05) {
                left_x = 0;
            }

            if (abs(right_x) <= 0.05) {
                right_x = 0;
            }

            // Calculate Mecanum powers for translation (Up, Down, Left, Right)
            double flPower = left_y + left_x + right_x;
            double blPower = left_y - left_x + right_x;
            double frPower = left_y - left_x - right_x;
            double brPower = left_y + left_x - right_x;

            // Normalize powers if stick magnitude exceeds 1.0
            double max = Math.max(-1.0,
                    Math.max(abs(flPower), Math.max(abs(blPower),
                            Math.max(abs(frPower), abs(brPower)))));
            flPower /= max;
            blPower /= max;
            frPower /= max;
            brPower /= max;

            double acceleration = 0.75 + (gamepad1.right_trigger * 0.25);

            motor_front_left.setPower(flPower * acceleration);
            motor_back_left.setPower(blPower * acceleration);
            motor_front_right.setPower(frPower * acceleration);
            motor_back_right.setPower(brPower * acceleration);

            // -------------------------------------------------------------
            // 2. INTAKE & SHOOTER MOTOR TOGGLES (Buttons A & X)
            // -------------------------------------------------------------
            boolean currentAState = gamepad1.a;
            if (currentAState && !lastAState) {
                intakeOn = !intakeOn;
            }
            lastAState = currentAState;

            boolean currentXState = gamepad1.x;
            if (currentXState && !lastXState) {
                shooterOn = !shooterOn;
            }
            lastXState = currentXState;

            motor_intake.setPower(intakeOn ? 0.667 : 0.0);
            motor_shooter.setPower(shooterOn ? -0.628 : 0.0);

            // -------------------------------------------------------------
            // 3. SERVO 40-DEGREE LIMIT CONTROL (Button B)
            // -------------------------------------------------------------
            if (gamepad1.b) {
                servo_intake.setPosition(SERVO_MAX_POS);
            } else {
                servo_intake.setPosition(SERVO_MIN_POS);
            }
        }
    }
}
