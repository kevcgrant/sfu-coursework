#!/bin/bash

# CMPT 471 Project
# Kevin Grant / 301192898
#
# Runs and times the logistics solver over many input options.
# Results are written to a CSV file.

_SOLVER="logistics.idp"
_SYMMETRY_CONSTRAINT="treeEdge(a,b) => a < b."
_GENERATOR="GraphGenerator"
_CSV_FILE="results.csv"

_ITERATIONS=3

_total_time=0.000

_checkForSymmetryConstraint()
{
  grep "$_SYMMETRY_CONSTRAINT" $_SOLVER | grep "//" > /dev/null
  if [ $? -ne 0 ]; then
    echo "Symmetry constraint is USED"
  else 
    echo "Symmetry constraint is NOT USED"
  fi
  echo ""
}

_addTime()
{
  local seconds=$(echo "$1" | grep real | grep -Eo [0-9]*\.[0-9]{3})
  local minutes=$(echo "$1" | grep real | grep -o [0-9]*m | grep -o [0-9]*)
  local time=$(echo "$minutes * 60" + $seconds | bc -l) 

  _total_time=$(echo $time + $_total_time | bc -l) 
}

_updateStructure()
{
  local nodes="$1"
  local density="$2"
  local destinations="$3"
  local graphData=$(java GraphGenerator -n "$nodes" -d "$density" -D "$destinations")
  
  local edges=$(echo "$graphData" | grep edge)
  local start=$(echo "$graphData" | grep start)
  local dests=$(echo "$graphData" | grep dest)

  sed -i "s/edge=.*/$edges/g" $_SOLVER
  sed -i "s/start=.*/$start/g" $_SOLVER
  sed -i "s/dest=.*/$dests/g" $_SOLVER
}

_runSolver()
{
  for i in $(seq 1 $_ITERATIONS); do 
    echo -n '.'
    _result=$({ time idp $_SOLVER 2>/dev/null; } 2>&1 | egrep "real|cost")
    _addTime "$_result"
  done;
}

_printAverageTime()
{
  _average=$(echo "$_total_time / ($_ITERATIONS * 3)" | bc -l)
  _average=${_average:0:6}
  echo -e "\rAverage time: $_average seconds"
  echo ""
  _total_time=0.000
}

_writeToCSV()
{
  echo "$1,$2,$3,$_average" >> $_CSV_FILE
}

_getResultsForOptions()
{
  if [ `echo "2 / $1" '>' $2 | bc -l` -eq 1 ]; then 
    return
  elif [[ $3 -ge $1 ]]; then
    return
  fi

  echo "NODES: $1  DENSITY: $2  DESTS: $3" 
  for i in {1..3}; do
    _updateStructure "$1" "$2" "$3"
    _runSolver
  done

  _printAverageTime
  _writeToCSV "$1" "$2" "$3"
}

_main()
{
  _checkForSymmetryConstraint
  > $_CSV_FILE
  echo "nodes,density,destinations,time" >> $_CSV_FILE

  local nodes="4 6 8 10 12 14 16 18 20"
  local densities="0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9 1.0"
  local destinations="1 2 4 8 10"

  for node in $nodes; do
    for density in $densities; do
      for dest in $destinations; do
        _getResultsForOptions "$node" "$density" "$dest"
      done
    done
  done
}

_main
