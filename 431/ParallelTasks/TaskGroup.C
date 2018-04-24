// Penntd=c++0x -pthread -lrt -Wall -O3 ppp.C random.C TaskGroup.C driver-sort.C -o driver-sortParallel Primitives Library
// Author: Prof. Milo Martin
// University of Pennsylvania
// Spring 2010

#include "ppp.h"
#include "TaskGroup.h"
#include "TTSLock.h"

namespace ppp {

  namespace internal {
    // Task-based scheduling variables
    TaskQueue* g_queues_ptr = NULL;
    atomic<int> g_stop_counter;  
  }

  using namespace internal;

  void TaskGroup::spawn(Task& t) {
    int id = get_thread_id();
    assert(g_queues_ptr != NULL);
    TaskQueue& queue = g_queues_ptr[id];  // ASSIGNMENT: use per-thread task queue with "get_thread_id()"
    m_wait_counter.fetch_and_inc();
    t.setCounter(&m_wait_counter);
    queue.enqueue(&t);
  }
  
  void process_tasks(const atomic<int>* counter)
  {
    int id = get_thread_id();
    int a;     
    int maxIndex = 0;
    int max;
    TTSLock m_lock;
    TaskQueue& queue = g_queues_ptr[id];  // ASSIGNMENT: use per-thread task queue with "get_thread_id()"

    while (counter->get() != 0) {
      if (id == 0) {
        PPP_DEBUG_EXPR(queue.size());}
      max = 0;
    
      // ASSIGNMENT: add task stealing
      // When queue is empty, find largest
      // queue and enqueue a task from it
      // to local queue

      if (g_queues_ptr[id].size() == 0) {
        
	for(a = 0; a < get_thread_count(); a++) {
     	  if (g_queues_ptr[a].size() > max) {
	    maxIndex = a;
            max = g_queues_ptr[a].size();
          }
	 }
         if (max > 1) {
           queue.enqueue(g_queues_ptr[maxIndex].steal());
         } 
         
      } 
       // Dequeue from local queue   
      Task* task = queue.dequeue();
      
      if (task != NULL) {
        task->execute(); // overloaded method
        task->post_execute(); // cleanup, method of base class
      }
    }
  }
}



