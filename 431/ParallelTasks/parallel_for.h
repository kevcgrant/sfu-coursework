// Penn Parallel Primitives Library
// Author: Prof. Milo Martin
// University of Pennsylvania
// Spring 2010

#ifndef PPP_FOR_H
#define PPP_FOR_H

#include "ppp.h"
#include "Task.h"
#include "TaskGroup.h"
#include "atomic.h"

namespace ppp {
	
  namespace internal {
    template <typename T>
    class DistributeTask: public ppp::Task {
    public:
      DistributeTask(int64_t start, int64_t end, T* functor, int64_t grainsize) {
        m_start = start;
        m_end = end;
        m_functor = functor;
        m_grainsize = grainsize; 
      } 

      void execute() {
        if (m_end-m_start <= 1) {
          return;
        } 
 
        // if the size of the task lists is smaller than the grainsize
        // tell function to do work for that specific list
        if (m_end-m_start < m_grainsize) {
          m_functor->calculate(m_start, m_end);
          return;
        }
        int64_t mid = ((m_end - m_start) /2) + m_start;
     
        // Splits computation into two halves until it is smaller than grainsize
        ppp::TaskGroup tg;
        DistributeTask t1(m_start, mid, m_functor, m_grainsize);
        DistributeTask t2(mid, m_end, m_functor, m_grainsize);
        tg.spawn(t1);
        tg.spawn(t2);
        tg.wait();
      }
    private:
      int64_t m_start;
	  int64_t m_end;
	  T* m_functor;
	  int64_t m_grainsize;
    };
  }
  template <typename T>
  extern inline
  void parallel_for(int64_t start, int64_t end, T* functor, int64_t grainsize=0)
  {

    // ASSIGNMENT: make this parallel via recursive divide and conquer
	internal::DistributeTask<T> t(start, end, functor, grainsize);
	t.execute();
	
    //functor->calculate(start, end);
  }
}

#endif
