#include "uart.h"
#include "move.h"
#include "open_interface.h"
#include "scanner.h"


//Bump messages the robot will send to putty
char bump_right[] = "RB\n\r";
char bump_left[] = "LB\n\r";

//Cliff messages the robot will send to putty
char cliff_front_left[] = "CFL\n\r";
char cliff_front_right[] = "CFR\n\r";
char cliff_left[] = "CL\n\r";
char cliff_right[] = "CR\n\r";

//Boundary notifications the robot will send to putty
char tape_front_left[] = "OOBFL\n\r";
char tape_front_right[] = "OOBFR\n\r";
char tape_left[] = "OOBL\n\r";
char tape_right[] = "OOBR\n\r";

//boundary for reflective value of the gray floor: Bot 4
int FLOORUPPERBOUND = 2500;
//boundary for non-reflective value of the drop: Bot 4
int FLOORLOWERBOUND = 1000;
//distance to move backwards
int MOVEBACKDISTANCE = 15;
//angle to turn
int TURNANGLE = 90;

int sum = 0;

/**
 * Move the robot forward a certain distance
 *
 * Checks if bump, cliff, or boundary detection has occurred and sends those to the driver via Putty
 **/
void move_forward(oi_t*sensor, int centimeters)
{

    sum = 0;

    //while the distance moved is less than the distance that we want to move, continue to move
    while (sum < centimeters*10)
    {
        //update the sensor data
        oi_update(sensor);

        //Set the wheels to move forward
        oi_setWheels(100, 100);

        //If a the sensor detects a left bump
        if (sensor->bumpLeft)
        {
            //stop the robot
            oi_setWheels(0, 0);

            //send the bump to putty
            uart_sendStr(bump_left);

            //move the robot backwards
            move_backward(sensor, MOVEBACKDISTANCE);

            //break out of the loop
            break;
        }

        //If a the sensor detects a right bump
        if (sensor->bumpRight)
        {
            //stop the robot
            oi_setWheels(0, 0);

            //send the bump to putty
            uart_sendStr(bump_right);

            //move back
            move_backward(sensor, MOVEBACKDISTANCE);

            //break out of the loop
            break;
        }

        //If the sensor detects a front right cliff
        if (sensor->cliffFrontRightSignal < FLOORLOWERBOUND)
        {
            //stop the robot
            oi_setWheels(0, 0);

            //send the cliff to putty
            uart_sendStr(cliff_front_right);

            //move back
            move_backward(sensor, MOVEBACKDISTANCE);

            //break out of the loop
            break;
        }

        //If the sensor detects a right cliff
        if (sensor->cliffRightSignal < FLOORLOWERBOUND)
        {
            //stop the robot
            oi_setWheels(0, 0);

            //send the cliff to putty
            uart_sendStr(cliff_right);

            //turn cw 90
            turn_clockwise(sensor, TURNANGLE);

            //break out of the loop
            break;
        }

        //If the sensor detects a front left cliff
        if (sensor->cliffFrontLeftSignal < FLOORLOWERBOUND)
        {
            //stop the robot
            oi_setWheels(0, 0);

            //send the cliff to putty
            uart_sendStr(cliff_front_left);

            //move back
            move_backward(sensor, MOVEBACKDISTANCE);

            //break out of the loop
            break;
        }

        //If the sensor detects a left cliff
        if (sensor->cliffLeftSignal < FLOORLOWERBOUND)
        {
            //stop the robot
            oi_setWheels(0, 0);

            //send the cliff to putty
            uart_sendStr(cliff_left);

            //turn ccw 90
            turn_clockwise(sensor, -TURNANGLE);

            //break out of the loop
            break;
        }

        //if the sensor detects tape on front right
        if (sensor->cliffFrontRightSignal > FLOORUPPERBOUND)
        {
            //stop the robot
            oi_setWheels(0, 0);

            //send the cliff to putty
            uart_sendStr(tape_front_right);

            //move back
            move_backward(sensor, MOVEBACKDISTANCE);

            //break out of the loop
            break;
        }

        //If the sensor detects tape on right
        if (sensor->cliffRightSignal > FLOORUPPERBOUND)
        {
            //stop the robot
            oi_setWheels(0, 0);

            //send the cliff to putty
            uart_sendStr(tape_right);

            //turn cw 90
            turn_clockwise(sensor, TURNANGLE);

            //break out of the loop
            break;
        }

        //If the sensor detects tape on front left
        if (sensor->cliffFrontLeftSignal > FLOORUPPERBOUND)
        {
            //stop the robot
            oi_setWheels(0, 0);

            //send the cliff to putty
            uart_sendStr(tape_front_left);

            //move back
            move_backward(sensor, MOVEBACKDISTANCE);

            //break out of the loop
            break;
        }

        //If the sensor detects tape on left
        if (sensor->cliffLeftSignal > FLOORUPPERBOUND)
        {
            //stop the robot
            oi_setWheels(0, 0);

            //send the cliff to putty
            uart_sendStr(tape_left);

            //turn ccw 90
            turn_clockwise(sensor, -TURNANGLE);

            //break out of the loop
            break;
        }

        //updates the distance that the robot has moved
        sum += sensor->distance;
    }

    oi_setWheels(0, 0); //stop
}

/**
 * Turn the robot x degrees clockwise or counterclockwise based on the sign of the x passed in
 */
void turn_clockwise(oi_t* sensor, int degrees)
{

    //set the degrees turned to 0
    sum = 0;
    sensor->angle = 0;

    //turn the robot counter-clockwise (LEFT)
    if (degrees > 0)
    {
        oi_setWheels(100, -100); //move forward; full speed

        //while the total angle turned is less than the angle that was sent to turn
        while (sum < degrees)
        {
            //updating the sensor data
            oi_update(sensor);
            //adding the angle to the sum
            sum += sensor->angle;
        }

        //stop the robot
        oi_setWheels(0, 0); //stop
    }

    //turn the robot clockwise (RIGHT)
    if (degrees < 0)
    {
        //move forward
        oi_setWheels(-100, 100);

        //updating the sensor data
        while (abs(sum) < abs(degrees))
        {
            //updating the sensor data
            oi_update(sensor);
            //adding the angle to the sum
            sum += sensor->angle;
        }

        //stop the robot
        oi_setWheels(0, 0);
    }
}

/**
 * Move the robot backwards a certain distance x cm
 */
void move_backward(oi_t*sensor, int centimeters)
{

    //the distance moved to 0
    sum = 0;

    //update the sensor data
    oi_update(sensor);

     //set wheels to full speed backwards
     oi_setWheels(-200, -200);

     //updating the sensor data
     while (-centimeters*10 < sum)
     {
         //update the sensor data
         oi_update(sensor);

         //updates the distance that the robot has moved
         sum += sensor->distance;
     }
     //stop the robot
     oi_setWheels(0, 0);
}
