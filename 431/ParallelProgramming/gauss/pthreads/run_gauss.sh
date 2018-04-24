#!/bin/bash

_GAUSS=/home/kcgrant/a1/1d_column_cyclic/gauss
_MATRICES_LOC=/home/kcgrant/a1/pthreads/input_matrices
_MATRICES="matrix_10.dat jpwh_991.dat matrix_2000.dat orsreg_1.dat sherman5.dat saylr4.dat sherman3.dat"
_THREADS="2 4 8 16"

_main()
{
  for matrix in $_MATRICES; do
    for thread in $_THREADS; do
    echo -e "\033[1mMATRIX: $matrix, THREADS: $thread\033[0m"
    echo ""
      for i in {1..2}; do
        $_GAUSS $_MATRICES_LOC/$matrix $thread
        echo ""
        sleep 3
      done
    done
  done
}

_main

