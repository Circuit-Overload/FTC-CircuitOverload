                                                                                                  package org.firstinspires.ftc.teamcode;

import static java.lang.Math.abs;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="TeleOp_Main", group="Linear OpMode")
public class ProtoTest1 extends LinearOpMode {

    // Hardware Declarations
    private DcMotor motor_front_left, motor_front_right, motor_back_left, motor_back_right;
    private DcMotor motor_intake;
    private Servo servo_intake_right, servo_intake_left;



    //Turning
    //



    // Toggle Tracking Variables
    private boolean intakeOn = false;
    private boolean lastAState = false;

    private boolean servosOn = false;
    private boolean lastBState = false;

    // Servo Positions (Adjust these values to match your physical build!)
    private final double SERVO_OFF_POS = 0.0;
    private final double SERVO_ON_POS = 1.0;

    @Override
    public void runOpMode() {

        // Hardware Mapping
        motor_front_left = hardwareMap.get(DcMotor.class, "motor_front_left");
        motor_back_left = hardwareMap.get(DcMotor.class, "motor_back_left");
        motor_front_right = hardwareMap.get(DcMotor.class, "motor_front_right");
        motor_back_right = hardwareMap.get(DcMotor.class, "motor_back_right");

        motor_intake = hardwareMap.get(DcMotor.class, "motor_intake");
        servo_intake_left = hardwareMap.get(Servo.class, "servo_intake_left");
        servo_intake_right = hardwareMap.get(Servo.class, "servo_intake_right");

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
            // double right_y = -gamepad1.right_stick_y;
            double right_x = gamepad1.right_stick_x;
            double rt_down = gamepad1.right_trigger;

            // add deadzone to robot  // discuss if deadzone is needed.
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
            double max = Math.max(1.0,
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
            // 2. INTAKE MOTOR TOGGLE (Button A)
            // -------------------------------------------------------------
            boolean currentAState = gamepad1.a;
            // Rising edge detection (triggers only once when pressed down)
            if (currentAState && !lastAState) {
                intakeOn = !intakeOn; // Toggle boolean state
            }
            lastAState = currentAState;

            // Set motor power based on toggle state
            motor_intake.setPower(intakeOn ? -100.0 : 0.0);

            // -------------------------------------------------------------
            // 3. BOTH SERVOS TOGGLE (Button B)
            // -------------------------------------------------------------
            boolean currentBState = gamepad1.b;
            if (currentBState && !lastBState) {
                servosOn = !servosOn; // Toggle boolean state
            }
            lastBState = currentBState;

            // Set servo positions based on toggle state
            double targetPos = servosOn ? SERVO_ON_POS : SERVO_OFF_POS;
            servo_intake_left.setPosition(targetPos);
            servo_intake_right.setPosition(targetPos);
        }
    }
}