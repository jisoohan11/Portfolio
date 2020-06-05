/*
 * scanner.c
 *
 *  
 *
 */

#include <adc.h>
#include <lcd.h>
#include <math.h>
#include <ping.h>
#include <pwm.h>
#include <stdio.h>
#include <Timer.h>
#include <uart.h>

//constant PI
#define PI 3.14159265

//variable for converting to radians
double rad = PI / 180;

//current position in degrees
int degrees = 0;

//arrays to store IR sensor data and sonar sensor data in
float IR_dist_arr[200];
float sonar_dist_arr[200];

//distance measured from IR sensor
float distance_IR = 0;

//# of objects
int obj_count = 0;

//Whether or not an object has begun -- 0 for false, 1 for true
int object_begin = 0;
//last angle taken of the object detected
int last_angle = 0;
//first angle taken of the object detected
int first_angle = 0;

//change in the angle width
int delta_angle = 0;

//keeps track of the delta_angle for each object
int angle_change[10];
int first_angle_arr[10];
int last_angle_arr[10];
float obj_dist_arr[10];

//the string to get sent to putty
char word[100];

//total distance away that the object is
float total_dist = 0.0;

float smallest_obj_width = 0.0;

//iteration of the for loop that we are on
int i = 0;
int j = 0;
int k = 0;
int l = 0;
int m = 0;

int smallest_obj = 0;


/**
 * Scan the surrounding area and send information about the objects back via putty
 */
void scan_area(void)
{
    //current position in degrees
    degrees = 0;

    //clear out the data arrays
    for(i = 0; i < 200; i++){
        IR_dist_arr[i] = 0;
        sonar_dist_arr[i] = 0;
    }

    //keeps track of the delta_angle for each object
    for(i = 0; i < 10; i++){
        angle_change[i] = 0;
        first_angle_arr[i] = 0;
        last_angle_arr[i] = 0;
        obj_dist_arr[i] = 0;
    }

    //clear the word array
    for(i = 0; i < 100; i++)
    {
        word[i] = '\0';
    }

    //distance measured from IR sensor
    distance_IR = 0.0;

    //# of objects
    obj_count = 0;

    //Whether or not an object has begun -- 0 for false, 1 for true
    object_begin = 0;
    //last angle taken of the object detected
    last_angle = 0;
    //first angle taken of the object detected
    first_angle = 0;

    //change in the angle width
    delta_angle = 0;

    //total distance away that the object is
    total_dist = 0.0;

    smallest_obj_width = 0.0;

    smallest_obj = 0;

    //lcd_printf("Smallest object:%d\nDistance:%4.2f\nObject Count:%d\n", smallest_obj, total_dist, obj_count);

    while (degrees < 181)
    {
        //send a pulse to the ping sensor
        pulse_servo(degrees);

        //read in the distance value from the IR Sensor
        distance_IR = adc_read();
        //store the IR distance in an array
        IR_dist_arr[degrees] = distance_IR;

        //send out a sonar pulse
        send_pulse();
        //store the sonar distance in an array -- cast to float since it is a double initially
        sonar_dist_arr[degrees] = distance_s;

        //increment the number of degrees you are at
        degrees += 1;

        timer_waitMillis(100);
    }

    /**
     * Data Analysis:
     * Detect the #objects, detect the smallest object, detect the accurate distance of the object, detect the object width
     */

    //for the length of the data array -- 180
    for (k = 0; k < 180; k++)
    {
        //if the IR_dist_arr data are within 50 cm and 10 cm
        if (IR_dist_arr[k] <= 50 && IR_dist_arr[k] >= 10)
        {
            //if an object hasn't started yet
            if (object_begin == 0)
            {
                //detect when the object starts and stops based on the IR_dist_arr by setting the first angle
                first_angle = k;
                //begin the object
                object_begin = 1;
            }
        }
        else if (IR_dist_arr[k] > 50)
        {
            if (object_begin == 1)
            {
                //reset the total distance for this new object
                total_dist = 0.0;

                //set the last angle
                last_angle = k - 1;

                //reset object_begin to 0
                object_begin = 0;

                //calculate the change in angle to determine what to divide our value by
                delta_angle = last_angle - first_angle;
                angle_change[obj_count] = delta_angle;

                first_angle_arr[obj_count] = first_angle;
                last_angle_arr[obj_count] = last_angle;

                //set l to start counting at the beginning of the object angle
                for (l = first_angle; l <= last_angle; l++)
                {
                    //adding
                    total_dist = total_dist + sonar_dist_arr[l];
                }

                //get the average distance away that the object is
                total_dist = total_dist / (float)delta_angle;

                //store the total distance, for the object that we are on
                obj_dist_arr[obj_count] = total_dist;

                //adding to the object count
                obj_count++;
            }
        }
    }

    //Array the length of object count to store the width of the objects
    double object_widths[obj_count];
    float median_angle = 0.0;
    //calculating the object widths based on the distance and the angle
    for (j = 0; j < obj_count; ++j)
    {
       // median_angle = (((float)last_angle[j] + (float)first_angle[j])/ 2.0);
//        median_angle = (float)(last_angle_arr[j] + first_angle_arr[j])/2.0;
        object_widths[j] = (double)(sonar_dist_arr[first_angle_arr[j]+1]) * sin((angle_change[j] / 2) * rad);
//        taking the data from the median angle of the object and multiplying by
//        float angle = (float)((median_angle - first_angle_arr[j]));
//        object_widths[j] = (double)(sonar_dist_arr[(int)(median_angle)] * tan(angle * rad) * 2);
    }

    //analyze the distance at the object angle
    for (m = 0; m < obj_count; m++)
    {
        if (object_widths[m] < object_widths[smallest_obj])
        {
            smallest_obj = m;
        }
    }

    // The width of the smallest object
    smallest_obj_width = (float)object_widths[smallest_obj];

    //If there is at least one object, the object count will be 1
    if (obj_count != 0)
    {
        smallest_obj = smallest_obj + 1;
    }

    //Print the data into the char string word
    sprintf(word, "Number of objects: %d\n\r", obj_count);
    uart_sendStr(word);

    //clear the word array
    for(i = 0; i < 100; i++)
    {
        word[i] = '\0';
    }

    for (m = 0; m < obj_count; m++)
    {
        sprintf(word, "Object: %d, D: %f, SA: %d, EA: %d, W: %lf\n\r", m+1, obj_dist_arr[m], first_angle_arr[m], last_angle_arr[m], object_widths[m]);
        uart_sendStr(word);
    }

    //Pulse the servo back to 0 once finished scanning
    pulse_servo(0);
}

