/*
*
*   ping.h
*
*   
*/

#ifndef PING_H_
#define PING_H_

volatile unsigned int changed_time;
//start time of the return pulse
volatile unsigned int rising_time;
//end time of the return pulse
volatile unsigned int falling_time;

volatile float distance_s;

void TIMER3B_Handler(void);
void ping_init(void);
void send_pulse();

#endif /* ADC_H_ */
