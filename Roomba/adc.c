/*
 *
 *  
 */

#include "Timer.h"
#include "math.h"
#include "adc.h"

/**
 * Initialize the ADC
 */

void adc_init(void){

    //enable ADC 0 module on port B
    SYSCTL_RCGCGPIO_R |= 0b010;

    //enable clock for ADC
    SYSCTL_RCGCADC_R |= 0x1;

    //enable port B pin 4 to work as alternate functions
    GPIO_PORTB_AFSEL_R |= 0b00010000;

    //set pin to input - 4
    GPIO_PORTB_DEN_R &= 0b11101111;

    //disable analog isolation for the pin 4
    GPIO_PORTB_AMSEL_R |= 0b00010000;

    //initialize the port trigger source as processor (default)
    GPIO_PORTB_ADCCTL_R = 0x00;

    //disable SS0 sample sequencer to configure it
    ADC0_ACTSS_R &= ~ADC_ACTSS_ASEN0;

    //initialize the ADC trigger source as processor (default)
    ADC0_EMUX_R = ADC_EMUX_EM0_PROCESSOR;

    //set 1st sample to use the AIN10 ADC pin
    ADC0_SSMUX0_R |= 0x000A;

    //enable raw interrupt status
    ADC0_SSCTL0_R |= (ADC_SSCTL0_IE0 | ADC_SSCTL0_END0);

    //enable oversampling to average
    ADC0_SAC_R |= ADC_SAC_AVG_64X;

    //re-enable ADC0 SS0
    ADC0_ACTSS_R |= ADC_ACTSS_ASEN0;
}

/**
 * Capture the distance in cm using the IR sensor
 */
float adc_read(){
    //the output voltage from the ADC
    int voltage_value = 0;

    //initiate SS0 conversion
    ADC0_PSSI_R = ADC_PSSI_SS0;

    //wait for ADC conversion to be complete
    while((ADC0_RIS_R & ADC_RIS_INR0) == 0){
        //wait
    }

    //grab result -- voltage
    voltage_value = ADC0_SSFIFO0_R;

    //calculation for the distance:
    //return 102260 * (pow(voltage_value, -1.178));
    return 66514 * (pow(voltage_value, -1.096));
}



