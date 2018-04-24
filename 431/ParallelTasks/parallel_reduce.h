// Penn Parallel Primitives Library
// Author: Prof. Milo Martin
// University of Pennsylvania
// Spring 2010

#ifndef PPP_REDUCE_H
#define PPP_REDUCE_H

#include "ppp.h"
#include "Task.h"
#include "TaskGroup.h"
#include "atomic.h"

namespace ppp {
 
  namespace internal {
    template <typename T>
    extern inline
    // Summate array
    T add(T* array, int64_t start, int64_t end) {  
      T sum;
      sum = 0;
      //printf("start = %lu, end = %lu\n", start, end);
      for (int i=start; i<end; i++) {
        sum = sum + array[i];
      }
      return sum;
    }

    template <typename T>
    class AddingTask: public ppp::Task {
    public:
      AddingTask(T* array, int64_t start, int64_t end, int64_t grainsize, T* sumArray) {
        m_array = array;
        m_start = start;
        m_end = end;
        m_grainsize = grainsize;
        m_sumArray = sumArray; 
      } 

      void execute() {
        if (m_end-m_start <= 1) {
          return;
        } 
 
        // if the size of the array is smaller than the grainsize
        // summate the array interval provided and store in array
        if (m_end-m_start < m_grainsize) {
          T sum;
          sum = add(m_array, m_start, m_end);
          m_sumArray[get_thread_id()] = m_sumArray[get_thread_id()] + sum;
          return;
        }
        int64_t mid = ((m_end - m_start) /2) + m_start;
     
        // Splits computation into two halves until it is smaller than grainsize
        ppp::TaskGroup tg;
        AddingTask t1(m_array, m_start, mid, m_grainsize, m_sumArray);
        AddingTask t2(m_array, mid,  m_end, m_grainsize, m_sumArray);
        tg.spawn(t1);
        tg.spawn(t2);
        tg.wait();
      }
    private:
      T* m_array;
      int64_t m_start;
      int64_t m_end;
      int64_t m_grainsize;
      T* m_sumArray;
    };
  }
  template <typename T>
  extern inline
  T parallel_reduce(T* array, int64_t start, int64_t end, int64_t grainsize)
  {
    // ASSIGNMENT: make this parallel via recursive divide and conquer
    T* sumArray = new int64_t[get_thread_count()];
    internal::AddingTask<T> t(array, start, end, grainsize, sumArray);   
    t.execute();
    
    // Summates values computed from threads 
    // Each thread is assigned a specific cell to store it's computations
    T sum = 0;
    for (int i=0; i<get_thread_count(); i++) {
      sum = sum + sumArray[i]; 
    }
    return sum;
  }
}

#endif
