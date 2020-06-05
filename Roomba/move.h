/*
 * move.h
 *
 * Authors: Lindsey Sleeth, Smruthi Sandhanam, Joel Ohge, Geon Hee Cho
 * Date: 04/08/2018
 */

#ifndef MOVE_H_
#define MOVE_H_

#include "open_interface.h"

//define function prototypes here
void move_forward(oi_t*sensor, int centimeters);
void turn_clockwise(oi_t * sensor, int degrees);
void move_backward(oi_t*sensor, int centimeters);

#endif /* MOVE_ */
