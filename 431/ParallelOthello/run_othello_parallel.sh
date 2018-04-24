#!/bin/bash

OTHELLO_AI="othello-good-parallel-ai.c"
OTHELLO="othello-parallel"
THREADS="1 2 4 8 16"

OUTPUT_FILE="part3_output.txt"

_updateDepth()
{
  sed -i "s/#define DEPTH .*/#define DEPTH ${1}/g" $OTHELLO_AI
}

_rebuild()
{
  make clean
  make
}

_runOthello()
{
  _threads=$1
  _depth=$2

  echo "THREADS: $_threads, DEPTH $_depth" >> $OUTPUT_FILE
  echo "" >> $OUTPUT_FILE

  for i in {1..5}; do
    CILK_NWORKERS=$_threads ./othello-parallel -cilk_set_worker_count=$_threads >> "$OUTPUT_FILE" 2>&1
  done

  echo "" >> $OUTPUT_FILE
  echo "" >> $OUTPUT_FILE
}

_main()
{
  for depth in {1..5}; do
    _updateDepth $depth
    _rebuild
    for thread in $THREADS; do
      _runOthello $thread $depth
    done
  done
}

_main
