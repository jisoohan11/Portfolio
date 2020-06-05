/**
*
*   @file  uart.c
*
*   
*/

#include "uart.h"

/**
 * @brief sets all necessary registers to enable the uart 1 module.
 */
void uart_init(void){
    //enable clock to GPIO, R1 = port B
    SYSCTL_RCGCGPIO_R |= SYSCTL_RCGCGPIO_R1;

    //enable clock to UART1, R1 = UART1. ***Must be done before setting Rx and Tx (SeeDataSheet)
    SYSCTL_RCGCUART_R |= SYSCTL_RCGCUART_R1;

    //enable alternate functions on port b pins 0 and 1
    GPIO_PORTB_AFSEL_R |= (BIT0 | BIT1);

    //enable Rx and Tx on port B on pins 0 and 1
    GPIO_PORTB_PCTL_R |= 0x00000011;

    //set pin 0 and 1 to digital
    GPIO_PORTB_DEN_R |= (BIT0 | BIT1);

    //set pin 0 to Rx or input
    GPIO_PORTB_DIR_R &= ~BIT0;

    //set pin 1 to Tx or output
    GPIO_PORTB_DIR_R |= BIT1;

    //calculate baudrate
    uint16_t iBRD = 8; //use equations
    uint16_t fBRD = 44;//use equations

    //turn off uart1 while we set it up
    UART1_CTL_R &= ~(UART_CTL_UARTEN);

    //set baud rate
    UART1_IBRD_R = iBRD;
    UART1_FBRD_R = fBRD;

    //set frame, 8 data bits, 1 stop bit, no parity, no FIFO
    UART1_LCRH_R = UART_LCRH_WLEN_8 ;

    //use system clock as source
    UART1_CC_R = UART_CC_CS_SYSCLK;

    //re-enable enable RX, TX, and uart1
    UART1_CTL_R = (UART_CTL_RXE | UART_CTL_TXE | UART_CTL_UARTEN);
}

/**
 * @brief Sends a single 8 bit character over the uart 1 module.
 * @param data the data to be sent out over uart 1
 */
void uart_sendChar(char data){
    //wait until there is room to send data
    // 0 == Transmitter is not full (If it is not transmitting anything currently)
    // 1 == If the FIFO is disabled (FEN is 0), the transmit holding register is full
    // If the FIFO is enabled (FEN is 1) the transmit FIFO is full
    while(UART1_FR_R & 0x20)
    {
        //DO NOTHING :)
    }
    //send data
    UART1_DR_R = data;
}

/**
 * @brief polling receive an 8 bit character over uart 1 module.
 * @return the character received or a -1 if error occured
 */
int uart_receive(void){
    char data = 0;

    //counters to force a break
    int count_to = 500000;
    int count = 0;

    //wait to receive
   while(UART1_FR_R & UART_FR_RXFE)
    {

    }

    //mask the 4 error bits and grab only 8 data bits
    data = (char)(UART1_DR_R & 0xFF);
    return data;
}

//THIS IS THE ECHO -- NOT FOR PART 1
/**
 * @brief sends an entire string of character over uart 1 module
 * @param data pointer to the first index of the string to be sent
 */
void uart_sendStr(const char *data){
    //length of the char array being passed in
    int data_len = strlen(data);

    //the char at the index
    char to_send;

    //iterator var
    int i = 0;

    //iterate through the char array being passed in for the length of it
    for(i = 0; i < data_len; i++)
    {
        //the character to send at the data
        to_send = data[i];

        //send the char at index to the string
        uart_sendChar(to_send);
    }
}
