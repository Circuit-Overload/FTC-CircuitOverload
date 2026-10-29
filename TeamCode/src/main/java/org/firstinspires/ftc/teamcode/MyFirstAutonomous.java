package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="My First Autonomous", group="Autonomous")
public class MyFirstAutonomous extends LinearOpMode {


    @Override
    public void runOpMode() {
        // --- 1. INITIALIZATION PHASE ---
        Movement robotMovement = new Movement(this);

        robotMovement.hardwareMapMotors("motor_front_left", "motor_back_left", "motor_front_right", "motor_back_right");


        waitForStart();

        // --- 3. AUTONOMOUS ACTIONS ---
        if (opModeIsActive()) {



            // strafe for 3 seconds left
            robotMovement.strafeForSeconds(-0.5, 3);

            // move forward three seconds
            robotMovement.straightForSeconds(-0.5, 3);

            // test turn for seconds

            robotMovement.turnForSeconds(-0.5, 3);



        }
    }
}