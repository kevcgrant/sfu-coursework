//--------------------------------------
//-		     FILE: bbuff.c    	       -
//- 	   AUTHOR: Kevin Grant         -
//-			       301192898           -
//--------------------------------------

#include "bbuff.h"
#include <stdlib.h>
#include <stdbool.h>
#include <stdio.h>
#include <pthread.h>
#include <semaphore.h>

// Generate mutex for critical section locking
pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;

// Initialize haveData and haveSpace semaphores
sem_t haveData;
sem_t haveSpace;

// Counter for the number of data in the array
int dataCount;

// Initialize a buffer array
void* buffer[QUEUE_SIZE];

void bbuff_init(void) 
{
	// set haveData to 0, and haveSpace to QUEUE_SIZE
	sem_init(&haveData, 0, 0);
	sem_init(&haveSpace, 0, QUEUE_SIZE);

	// initialize the counter
	dataCount = 0;
}

void bbuff_blocking_insert(void* item)
{	
	// Waits for the buffer to have space, and locks the mutex
	sem_wait(&haveSpace);
	pthread_mutex_lock(&mutex);

	// Add data to the buffer and increment dataCount
	buffer[dataCount] = item;
	dataCount++;

	// Release the mutex lock, and unlock the haveData semaphore
	pthread_mutex_unlock(&mutex);
	sem_post(&haveData);

}

void* bbuff_blocking_extract(void)
{
	// Waits for the buffer to have data, and locks the mutex
	sem_wait(&haveData);
	pthread_mutex_lock(&mutex);

	// Extract data from the buffer
	void* item = buffer[dataCount-1];
	dataCount--;

	// Release the mutex lock, and unlock the haveSpace semaphore
	pthread_mutex_unlock(&mutex);
	sem_post(&haveSpace);

	// return the candy
	return item;
}

_Bool bbuff_is_data_available(void)
{
	if(dataCount > 0) {
		return true;
	}
	else {
		return false;
	}
}