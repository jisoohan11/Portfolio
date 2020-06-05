/*
 * pwm.c
 *
 *  Created on: Mar 22, 2018
 *      s
 *
 *      Preset the time of each event and use hardware to
generate the waveform
 */

#include "timer.h"
#include "pwm.h"

// start value in clock cycles --TODO: should this be unsigned??
unsigned int pwm_period = 0x04E200;
/**
 * Initialize GPIO & PWM output
 */
void servo_init(void)
{
    //Setting up the GPIO
    //1. Enable the clock to the port by setting the appropriate bits in the RCGCGPIO register (see page 340).
    //turn on PORTB system clock
    SYSCTL_RCGCGPIO_R |= 0b10;

    //2. Set the direction of the GPIO port pins by programming the GPIODIR register. A write of a 1
    //indicates output and a write of a 0 indicates input.
    //Setting PB5 to output
    GPIO_PORTB_DIR_R |= 0b100000;

    //3. Configure the GPIOAFSEL register to program each bit as a GPIO or alternate pin. If an alternate
    //pin is chosen for a bit, then the PMCx field must be programmed in the GPIOPCTL register for
    //the specific peripheral required.
    //setting a bit to 1 enables the alternate function for that pin --using alternate function for pin 5
    GPIO_PORTB_AFSEL_R |= 0b100000;

    //port control -- setting the digital function (PMCx Bit Field Encoding) Pg. 651 in data sheet
    GPIO_PORTB_PCTL_R |= 0x700000;

    //6. To enable GPIO pins as digital I/Os, set the appropriate DEN bit in the GPIODEN register. To
    //enable GPIO pins to their analog function (if available), set the GPIOAMSEL bit in the
    //GPIOAMSEL register.
    GPIO_PORTB_DEN_R |= 0b100000;   //Enabling PB5


        //Enable clock TIMER1

        //To use a GPTM, the appropriate TIMERn bit must be set in the RCGCTIMER or RCGCWTIMER
        //register (see page 338 and page 357). If using any CCP pins, the clock to the appropriate GPIO
        //module must be enabled via the RCGCGPIO register (see page 340). To find out which GPIO port
        //to enable, refer to Table 23-4 on page 1344. Configure the PMCn fields in the GPIOPCTL register to
        //assign the CCP signals to the appropriate pins (see page 688 and Table 23-5 on page 1351).

    SYSCTL_RCGCTIMER_R |= SYSCTL_RCGCTIMER_R1;

    //Configuring for Input Edge Time Mode -- Input capture mode
    //1. Ensure the timer is disabled (the TnEN bit is cleared) before making any changes.
    //Configure the timer for input capture mode

    // Configure the type of event(s) that the timer captures by writing the TnEVENT field of the GPTM Control (GPTMCTL) register.
        //TIMER1_CTL_R (GPTM Control): Enable, Edge Select -- enable both edges -- 0x3 - Used with CFG and MTBMR to control timer configuration,
    //specifically setting the trigger for the event.
    //disable timerB to allow us to change settings
    //0 to disable, 1 to enable and start counting -- bit 8
    TIMER1_CTL_R &= 0b011111111;

    ///TIMER1_CFG_R (GPTM Configuration): 16-bit mode - Value written to this register determines what mode GPTM is in (we are doing 16-bit)
    TIMER1_CFG_R |= 0b100;

    //TIMER1_TBMR_R (Timer B Mode Register): Count Down, Periodic mode, Alternate Mode Selection - Controls the modes for the Timer B when used individually
    //Timer Countdown is Bit 4 - Set to 0, Periodic Mode Bit 1 & 0 - Set to 0x2 (Periodic Timer Mode), Alternate Mode Selection PWM Mode Enable Bit 3 - Set to 1
    TIMER1_TBMR_R = (TIMER1_TBMR_R | 0b01010) & 0xFFFE;

    //TIMER3_TBILR_R (GPTM Timer B Interval Load) : Set upper bound - When the timer is counting down, this register is used to load the starting count value into the timer.
    TIMER1_TBILR_R = pwm_period & 0xFFFF;

    TIMER1_TBPR_R = pwm_period >> 16;

    //set to the period - 9680 for 0 degrees
    TIMER1_TBMATCHR_R = pwm_period - 5979;

    //Prescale Match
    TIMER1_TBPMR_R = (pwm_period - 5979) >> 16;

    //re-enable the timer
    TIMER1_CTL_R |= 0b100000000;
}

/**
 * Move the servo based on the position command given -- position in degrees
 */
void pulse_servo(double x){

	//Match value
	int match = 9680;

	/*
		y = mx + b;
		m = slope of clock cycles / degrees. 
		16Mhz * .001 = 16,000    <-- Pulsing 1 ms to zero degrees
		16Mhz * .0015 = 24,000   <-- Pulsing 1.5 ms to 90 degrees
		16Mhz * .002 = 32,000    <-- Pulsing 2 ms to 180 degrees

		(32000 - 16000)/(180-0) = 88.88 clock cycles per degree

		b = y-intercept, or minimum value which is equal to 16000 clock cycles

		x = degrees we want to move by

		y = clock cycles we want to pulse it by

	*/
		//double m = 158.0;
	    //double m = 162.8; -- bot 8
	    //double m = 151.87; //bot 2
	    //double m = 161.97; // bot 4
	    double m =155.65; //bot 9

		//int b = 9680;
	    //int b = 8023; -- bot 8
	    //int b = 7190; // bot 2
	    int b = 5979; //bot 9
		match = (int)(m * x) + b;

		//calibrate the match value if need
		//match = match + error_in_degrees

   		//these are the values that are going to change base on the button being pushed
        TIMER1_TBMATCHR_R = pwm_period - match;

        //Prescale Match
        TIMER1_TBPMR_R = (pwm_period - match) >> 16;

        //setting the length of the pulse in milliseconds
        pulse_len = match;
}


