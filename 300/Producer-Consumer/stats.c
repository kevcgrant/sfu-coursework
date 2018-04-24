//--------------------------------------
//-		     FILE: stats.c   	       -
//- 	   AUTHOR: Kevin Grant         -
//-			       301192898           -
//--------------------------------------

#include "stats.h"
#include <stdlib.h>
#include <stdio.h>

typedef struct {
	int factory;
	int number_made;
	int number_eaten;
	double min_delay;
	double avg_delay;
	double max_delay;
} stats_t;

stats_t *stats_array;

// Determines which factory is the last one
int last_factory;

void stats_init(int num_producers)
{
	// Dynamically allocate array
	stats_array = (stats_t*) malloc(num_producers*sizeof(stats_t));

	// Initialize array values
	for(int i = 0; i < num_producers; i++) {
		stats_array[i].factory = i;
		stats_array[i].number_made = 0;
		stats_array[i].number_eaten = 0;
		stats_array[i].min_delay = 0.0;
		stats_array[i].avg_delay = 0.0;
		stats_array[i].max_delay = 0.0;

		if(i == num_producers - 1) {
			last_factory = i;
		}
	}
}

void stats_cleanup(void)
{
	free(stats_array);
}

void stats_record_produced(int factory_number)
{
	int i = 0;
	//increment the number made by the factory
	do {
		if(stats_array[i].factory == factory_number) {
			stats_array[i].number_made++;
		}
		i++;
	} while(stats_array[i-1].factory != factory_number);
}

void stats_record_consumed(int producer_number, double delay_in_ms)
{
	int i = 0;
	// Record stats for number eaten and all delays
	do {
		if(stats_array[i].factory == producer_number) {

			// for the average calculation
			int n = stats_array[i].number_eaten;

			// Increment number eaten
			stats_array[i].number_eaten++;

			// Calculate delays

			// Set min delay: if this delay is smallest, or the first eaten, set as min
			if((delay_in_ms < stats_array[i].min_delay) || stats_array[i].number_eaten == 1) {
				stats_array[i].min_delay = delay_in_ms;
			}
			// Set max delay: if this delay is largest, set as max
			if(delay_in_ms > stats_array[i].max_delay) {
				stats_array[i].max_delay = delay_in_ms;
			}
			// Calculate average
			// new_avg = (curr_avg*old_num_eaten + delay) / new_num_eaten
			stats_array[i].avg_delay = (stats_array[i].avg_delay*n + delay_in_ms)/(n+1);
		}
		i++;
	} while(stats_array[i-1].factory != producer_number);

}

void stats_display(void)
{
	// title line
	printf("Statistics:\n");
	printf("%11s%8s%9s%15s%15s%15s\n", "Factory#", "#Made", "#Eaten", "Min Delay[ms]", "Avg Delay[ms]", "Max Delay[ms]");
	
	for(int i = 0; i <= last_factory; i++) {

		int f =	stats_array[i].factory;
		int made = stats_array[i].number_made;
		int eaten =	stats_array[i].number_eaten;
		double min = stats_array[i].min_delay;
		double avg = stats_array[i].avg_delay;
		double max = stats_array[i].max_delay;

		// data lines
		printf("%11d%8d%9d%15.5f%15.5f%15.5f\n", f, made, eaten, min, avg, max);
	}

	// Detect mistmatch
	for(int i = 0; i <= last_factory; i++) {
		if(stats_array[i].number_made != stats_array[i].number_eaten) {
			printf("ERROR: mismatch between number made and number eaten for factory %d.\n", i);
		}
	}
}