//--------------------------------------
//-		     FILE: bbuff.h    	       -
//- 	   AUTHOR: Kevin Grant         -
//-			       301192898           -
//--------------------------------------

#ifndef BBUFF_H
#define BBUFF_H

#define QUEUE_SIZE 10

/**
 * Initialize the bounded buffer
 */
void bbuff_init(void);

/**
 * Insert a candy into the bounded buffer
 *
 * @param item		item to be inserted into the buffer
 */
void bbuff_blocking_insert(void* item);

/**
 * Extract a candy from the bounded buffer
 */
void* bbuff_blocking_extract(void);

/**
 * Check if the buffer has data
 *
 * @return		true if the buffer has data, false otherwise
 */
_Bool bbuff_is_data_available(void);

#endif