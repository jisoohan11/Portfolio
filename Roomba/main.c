#include "move.h" //cybot movement functions
#include "uart.h"
#include "adc.h"
#include "pwm.h"
#include "ping.h"
#include "open_interface.h"
#include "music.h"
#include "button.h"
#include "scanner.h"



void main()
{
    oi_t* sensor = oi_alloc();
    oi_init(sensor);

    //initialize features
    uart_init(); //initialize the UART for wifi communication

    //Initializations for PING sensor, ADC and servo in order to scan the area
    adc_init();
    servo_init();
    ping_init();

    //Wait for the initialization of the servo to be complete
    timer_waitMillis(600);

    lcd_init();
    load_songs();

    //char for saving the last character received
    char temp = '\0';

    while (1)
    {
        //get character
        temp = uart_receive();
        //move forward
        if (temp == 'w')
        {
            move_forward(sensor, 10);
            uart_sendStr("Moving forwards\n\r");
        }
        //turn left
        if (temp == 'a')
        {
            turn_clockwise(sensor, 45);
            uart_sendStr("Turning left\n\r");
        }
        //turn right
        if (temp == 'd')
        {
            turn_clockwise(sensor, -45);
            uart_sendStr("Turning right\n\r");
        }
        if (temp == 'j')
        {
            turn_clockwise(sensor, 9);
            uart_sendStr("Turning left\n\r");
        }
        //turn right
        if (temp == 'k')
        {
            turn_clockwise(sensor, -9);
            uart_sendStr("Turning right\n\r");
        }
        //move backward
        if (temp == 's')
        {
            move_backward(sensor, 10);
            uart_sendStr("Moving backward\n\r");
        }
        //scan -- x corresponds to the key on the keyboard to control the robot
        if (temp == 'x')
        {
            //Let the driver know
            uart_sendStr("Scanning area\n\r");
            //Scan the area and send information back via putty to the driver
            scan_area();

        }
        //play notes -- z corresponds to the key on the keyboard to control the robot
        if (temp == 'z')
        {
            //Play the first song -- maybe we can have it cycle through other songs or play a song based on whether it has reached the goal or not
            oi_play_song(0);
            //Sending information back via putty to the driver
            uart_sendStr("Playing music\n\r");
        }
    }


    //Testing for individual components of the program
    //    Save for cliff sensor testing
    //    while (1)
    //    {
    //        oi_update(sensor_data); //maybe this should be done in the actual file -- not sure
    //        int cliffR = sensor_data->cliffRightSignal;
    //        int cliffFR = sensor_data->cliffFrontRightSignal;
    //        int cliffL = sensor_data->cliffLeftSignal;
    //        int cliffFL = sensor_data->cliffFrontLeftSignal;
    //        lcd_printf("R %d, FR %d, L %d, FL %d", cliffR, cliffFR, cliffL, cliffFL);
    //        timer_waitMillis(300);
    //    }
    //
    //    for testing song with button 1
    //        button_init();
    //
    //
    //        while (1)
    //        {
    //            int button = button_getButton();
    //            if (button == 1) //if button 1 is pressed,
    //            {
    //                oi_play_song(0);
    //            }
    //        }
}

