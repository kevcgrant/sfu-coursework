#!/bin/bash

OTHELLO_AI="othello-good-parallel-ai.c"
OTHELLO="othello-parallel"
CILKVIEW_DIR="cilkview"

_updateDepth()
{
  sed -i "s/#define DEPTH .*/#define DEPTH ${1}/g" $OTHELLO_AI
}

_rebuild()
{
  make clean
  make
}

_runCilkview()
{
  cilkview ./$OTHELLO > $CILKVIEW_DIR/cilkview${1}.txt 2>&1
}

_main()
{
  for i in {1..8}; do
    _updateDepth $i
    _rebuild
    _runCilkview $i
  done
}

_main
