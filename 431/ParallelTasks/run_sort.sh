#!/bin/bash

_THREADS=4
_GRAINSIZES="40000000 30000000 20000000 10000000 5000000 2500000 1250000 1000000 900000 800000 700000 600000 500000 400000"
_TRIALS=5

_main()
{
  for grainsize in $_GRAINSIZES; do
    ./driver-sort --particles 40000000 --trials $_TRIALS --grainsize $grainsize --threads $_THREADS | grep average
  done
}

_main
