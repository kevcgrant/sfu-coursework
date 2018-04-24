//--------------------------------------
//-		     FILE: stats.h   	       -
//- 	   AUTHOR: Kevin Grant         -
//-			       301192898           -
//--------------------------------------

#ifndef STATS_H
#define STATS_H

/**
 * Initialize an array for storing all statistics
 */ 
void stats_init(int num_producers);

/**
 * Cleanup any dynamically allocated memory 
 */
void stats_cleanup(void);

/**
 * Counts the number of candies produced by each factory
 *
 * @param factory_number	identifier for each factory
 */
void stats_record_produced(int factory_number);

/**
 * Counts the number of candies consumed from each factory
 *
 * @param producer_number	identifier for the factory that produced the candy
 * @param delay_in_ms		time the candy lived
 */
void stats_record_consumed(int producer_number, double delay_in_ms);

/**
 * Display the stats in a readable format
 */
void stats_display(void);

#endif