//--------------------------------------
//-		     FILE: candykids.c 	       -
//- 	   AUTHOR: Kevin Grant         -
//-			       301192898           -
//--------------------------------------


#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include <pthread.h>
#include <ctype.h>
#include <pthread.h>
#include <unistd.h>
#include <time.h>
#include "bbuff.h"
#include "stats.h"

// Used to determine when to stop the factory thread
_Bool stop_thread = false;

/**
 * Data type representing the candy
 * source_thread: 	 	tracks which factory produced the candy
 * time_stamp_in_ms:	tracks when the item was created
 */
typedef struct {
	int source_thread;
	double time_stamp_in_ms;
} candy_t;

/**
 * Gets the current time in milliseconds
 */
double current_time_in_ms(void)
{
	struct timespec now;
	clock_gettime(CLOCK_REALTIME, &now);
	return now.tv_sec * 1000.0 + now.tv_nsec/1000000.0;
}

/**
 * factory thread that produces candy
 */
void *candy_factory(void *factory_number)
{
	int number = *(int*)factory_number;

	while(!stop_thread) {

		// number of seconds to later wait: random # in domain [0, 3]
		int seconds = rand() % 4; 

		// print status message
		printf("\tFactory %d ship candy & wait %d seconds\n", number, seconds);
		
		// Dynamically allocate new candy item and populate its fields
		candy_t *candy = malloc(sizeof(candy_t));
		candy->time_stamp_in_ms = current_time_in_ms();
		candy->source_thread = number;

		// Add the candy to the bounded buffer
		bbuff_blocking_insert(candy);

		// Add this candy to its stats
		stats_record_produced(number);

		// Sleep for number of seconds determined randomly
		sleep(seconds);
	}

	printf("Candy-factory %d done\n", number);

	return 0;
}

/**
 * Kid thread that consumes candy
 */
void *kid() 
{
	while(true) {

		// Extract a candy from the bounded buffer
		candy_t* candy = (candy_t*)bbuff_blocking_extract();

		// Process the item
		double delay = current_time_in_ms() - candy->time_stamp_in_ms;
		int number = candy->source_thread;
		stats_record_consumed(number, delay);

		// candy has been processed, so free it
		free(candy);

		// Sleep for either 0 or 1 second
		sleep(rand() % 2);
	}

	return 0;
}

/**
 * Prints directions on using candykids.c 
 * Called if user inputed incorrect arguments
 */
void print_usage()
{
	printf("Usage: ./candykids <#factories> <#kids> <#seconds>\n");
	printf("#factories:\tnumber of candy-factory threads to spawn\n");
	printf("#kids:\t\tnumber of kids threads to spawn\n");
	printf("#seconds:\tnumber of seconds to allow the factory threads to run for\n");
	printf("All parameters must be integers greater than 0.\n\n");
}

int main(int argc, char **argv) 
{
	/* Extract arguments */

	// Check for invalid number of arguments
	if(argc != 4) {
		printf("Invalid number of arguments.\n\n");
		print_usage();
		exit(-1);
	}

	// Check for argument is of invalid type
	for(int i = 1; i < argc; i++) {
		if(atoi(argv[i]) <= 0) {
			printf("Invalid argument type.\n\n");
			print_usage();
			exit(-1);
		}
	}

	int factories = atoi(argv[1]);
	int kids = atoi(argv[2]);
	int seconds = atoi(argv[3]);

	/* Initialize modules */
	bbuff_init();
	stats_init(factories);

	/* Launch factory threads */

	// Array for holding candy factory thread ids
	pthread_t factory_tid[factories];

	// Array for holding factory numbers: holds numbers 0 to factories - 1
	int factory_array[factories];
	for(int i = 0; i < factories; i++) {
		factory_array[i] = i;
	}

	// Spawn 'factories' factory threads (number requested by arg passed)
	// Pass each factory its factory number (i)
	for(int i = 0; i < factories; i++) {
		pthread_create(&factory_tid[i], NULL, candy_factory, &(factory_array[i]));
	}

	/* Launch kid threads */

	// Array for holding kid thread ids
	pthread_t kid_tid[kids];
	memset(kid_tid, 0, kids*sizeof(int));

	// Spawn 'kids' kid threads (number requested by arg passed)
	for(int i = 0; i < kids; i++) {
		pthread_create(&kid_tid[i], NULL, kid, NULL);
	}

	/* Wait for requested time */
	for(int i = 0; i < seconds; i++) {
		printf("Time %ds\n", i);
		sleep(1);

	}

	/* Stop factory threads */

	printf("\nStopping candy factories...\n");
	stop_thread = true;

	for(int i = 0; i < factories; i++) {
		pthread_join(factory_tid[i], NULL);
	}

	/* Wait until no more candy */
	while(bbuff_is_data_available()) {
		printf("Waiting for all candy to be consumed\n");
		sleep(1);
	}

	/* Stop kid threads */

	printf("Stopping kids.\n\n");

	for(int i = 0; i < kids; i++) {
		pthread_cancel(kid_tid[i]);
		pthread_join(kid_tid[i], NULL);
	}

	/* Print statistics */
	stats_display();

	/* Cleanup any allocated memory */
	stats_cleanup();

	return 0;
}
