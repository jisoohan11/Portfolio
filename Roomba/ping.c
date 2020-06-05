/**
 * Ping.c
 *
 * Sends pulse to Ping sensor 
 *
 * 
 */

//including header files for timer, lcd, and uart which are all utilized in the program 
#include "timer.h"
#include "lcd.h"
#include "ping.h"
#include <stdbool.h>
#include "driverlib/interrupt.h"

//state 0 is LOW -- waiting to rise, 1 is the inbetween period and will be done 
volatile int state = 0;

/**
 *  ISR: Record the current event time
 */
void TIMER3B_Handler(void){
    if(state == 0)
        {
            // depending on current status
            rising_time = TIMER3_TBR_R & 0xFFFFFF;
            state = 1;
        }
        else
        {
            // depending on current status
            falling_time = TIMER3_TBR_R & 0xFFFFFF;
            //do nothing
            state = 2;

            //do calculation
            changed_time = (falling_time - rising_time) ;

            //check for overflow
            //if the falling time < rising time --> overflow
            if(falling_time < rising_time)
            {
                 changed_time = (changed_time + (1 << 24));
            }

            TIMER3_CTL_R &= ~(0x100);
        }

        //calculation for distance
        distance_s = (changed_time / 16000000.0) * 34000.0 / 2.0;

        //clear the interrupt by writing a 1
        TIMER3_ICR_R |= 0b10000000000;
}


/*
*set up the timer and configure it for input capture using Input Edge-Time Mode
*		
*		Timer register used in order to do input capture: 
*		GPTMCTL: GPTM (General Purpose Timer) Control
*       GPTMCFG: GPTM Configuration
*       GPTMTnMR: GPTM Timer n Mode (n is A or B)
*       GPTMTnILR: GPTM Timer n Interval Load
*       GPTMIMR: GPTM Interrupt Mask Register
*       GPTMMIS: GPTM Masked Interrupt Status
*       GPTMICR: GPTM Interrupt Clear Register
*/

void ping_init(void)
{
	//Setting up the GPIO
	//1. Enable the clock to the port by setting the appropriate bits in the RCGCGPIO register (see page 340).
    //turn on PORTB system clock
    SYSCTL_RCGCGPIO_R |= 0b10;

    //2. Set the direction of the GPIO port pins by programming the GPIODIR register. A write of a 1
    //indicates output and a write of a 0 indicates input.
    //Setting PB0-PB1 to input
    GPIO_PORTB_DIR_R &= ~0b1000;

    //3. Configure the GPIOAFSEL register to program each bit as a GPIO or alternate pin. If an alternate
    //pin is chosen for a bit, then the PMCx field must be programmed in the GPIOPCTL register for
    //the specific peripheral required.
    //setting a bit to 1 enables the alternate function for that pin --using alternate function for pin 3
    GPIO_PORTB_AFSEL_R |= 0b1000;

    //port control -- setting the digital function (PMCx Bit Field Encoding) Pg. 651 in data sheet
    GPIO_PORTB_PCTL_R |= 0x7000;

    //6. To enable GPIO pins as digital I/Os, set the appropriate DEN bit in the GPIODEN register. To
    //enable GPIO pins to their analog function (if available), set the GPIOAMSEL bit in the
    //GPIOAMSEL register.
    GPIO_PORTB_DEN_R |= 0b1000;   //Enabling PB0-PB1

    //When an edge is detected at input capture pin, current
    //TIMERx_TnV_R (GPTMTnV) value is captured
    //(saved) into TIMERx_TnR_R (GPTMTnR)

    //Time is captured immediately (when an event happens) and read by the CPU later
    /*
    	//Enable clock TIMER3 on clock to TIMER3

	    //To use a GPTM, the appropriate TIMERn bit must be set in the RCGCTIMER or RCGCWTIMER
	    //register (see page 338 and page 357). If using any CCP pins, the clock to the appropriate GPIO
	    //module must be enabled via the RCGCGPIO register (see page 340). To find out which GPIO port
	    //to enable, refer to Table 23-4 on page 1344. Configure the PMCn fields in the GPIOPCTL register to
	    //assign the CCP signals to the appropriate pins (see page 688 and Table 23-5 on page 1351).
	*/
    SYSCTL_RCGCTIMER_R |= SYSCTL_RCGCTIMER_R3;

    //Configuring for Input Edge Time Mode -- Input capture mode
    //1. Ensure the timer is disabled (the TnEN bit is cleared) before making any changes.
    //Configure the timer for input capture mode

    //disable timerB to allow us to change settings
    //0 to disable, 1 to enable and start counting -- bit 8
    TIMER3_CTL_R &= 0b011111111;

    //2. Write the GPTM Configuration (GPTMCFG) register with a value of 0x0000.0004.
    ///TIMER3_CFG_R (GPTM Configuration): 16-bit mode - Value written to this register
    //determines what mode GPTM is in (we are doing 16-bit individual, split timers)
    TIMER3_CFG_R |= 0b100;

    //3. In the GPTM Timer Mode (GPTMTnMR) register, write the TnCMR field to 0x0 and the TnMR field to 0x3.
    //TIMER3_TBMR_R (Timer B Mode Register): Capture mode, Edge-time mode, Count up - Controls the modes for the Timer B when used individually
    TIMER3_TBMR_R |= 0b10111;

    //4. Configure the type of event(s) that the timer captures by writing the TnEVENT field of the GPTM
    //Control (GPTMCTL) register.
    //TIMER3_CTL_R (GPTM Control): Enable, Edge Select -- enable both edges -- 0x3 - Used with CFG and MTBMR to control timer configuration, specifically setting the trigger for the event.
    TIMER3_CTL_R |= 0b110000000000;

    //TIMER3_TBILR_R (GPTM Timer B Interval Load) : Set upper bound - When the timer is counting down, this register is used to load the starting count value into the timer.
    TIMER3_TBILR_R = 0xFFFF;

    TIMER3_TBPR_R = 0xFF;

    //TIMER3_IMR_R (GPTM Interrupt Mask): Enable capture interrupt - setting or clearing bits to enable/disable controller-level interrupts.
    TIMER3_IMR_R |= 0b10000000000;

   	//Set the bit that corresponds to interrupt 36 
   	NVIC_EN1_R |= 0b10000;

   	//Bind Timer3B interrupt requests to your interrupt handler
	IntRegister(INT_TIMER3B, TIMER3B_Handler);

	// Enable Timer3B after setting it up -- Enables sending the interrupts 
	// Enable global interrupts
	IntMasterEnable();

    //re-enable the timer 
    //TIMER3_CTL_R |= 0b100000000;
}

/**
 * Send a start pulse
 */
void send_pulse(){
    //serial_init_timer();
    //when you send a pulse it can accidentally trigger an interrupt
    //how to prevent: 1. Disable the timer -- this one might take too long to propogate, so the safest bet is to go with disabling interrupts for the timer
    //				  2. Disable interrupts for the timer 

    //TIMER3_CTL_R &= 0b011111111;
	//disabling interrupts 
    //TIMER3_IMR_R &= ~0b10000000000;

    //disable alternate functions
    GPIO_PORTB_AFSEL_R &= 0b11110111;
    GPIO_PORTB_PCTL_R &= ~0x7000;

    GPIO_PORTB_DIR_R |= 0x08; // set PB3 as output
	GPIO_PORTB_DATA_R |= 0x08; // set PB3 to high -- set to 1

    timer_waitMicros(5);

	GPIO_PORTB_DATA_R &= 0xF7; // set PB3 to low
	GPIO_PORTB_DIR_R &= 0xF7; // set PB3 as input

	//re-enable alternate functions
	GPIO_PORTB_PCTL_R |= 0x7000;
    GPIO_PORTB_AFSEL_R |= 0b1000;

    state = 0;

    //re-enable interrupts
    //TIMER3_IMR_R |= 0b10000000000;
    TIMER3_CTL_R |= 0b100000000;

    while(state != 2)
    {
        //do nothing
    }
}



