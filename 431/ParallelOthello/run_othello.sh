#!/bin/bash

_X=0
_O=0
_T=0

_printResults()
{
    echo "X: $_X"
    echo "O: $_O"
    echo "T: $_T"
}

_main()
{
  for i in {0..999}; do
    output=$(./othello-serial | grep wins | grep -o -e [0-9])
    _x=$(echo $output | awk '{ print $1 }')
    _o=$(echo $output | awk '{ print $2 }')

    if [[ $_x -lt $_o ]]; then
        _O=$((_O+1))
    elif [[ $_x -gt $_o ]]; then
        _X=$((_X+1))
    else
        _T=$((_T+1))
    fi
  done
}

_main
_printResults
