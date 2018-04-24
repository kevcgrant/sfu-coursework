#!/bin/bash

OTHELLO_AI="othello-good-ai.c"
OTHELLO="othello-serial"

OUTPUT_FILE="part3_output_serial.txt"

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
  _depth=$1

  echo "DEPTH $_depth" >> $OUTPUT_FILE
  echo "" >> $OUTPUT_FILE

  for i in {1..5}; do
    ./othello-serial >> "$OUTPUT_FILE" 2>&1
  done

  echo "" >> $OUTPUT_FILE
  echo "" >> $OUTPUT_FILE
}

_main()
{
  for depth in {1..5}; do
    _updateDepth $depth
    _rebuild
    _runOthello $depth
  done
}

_main
