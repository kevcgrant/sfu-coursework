#!/bin/bash

# CMPT 431 Assignment 5
# Kevin Grant / 301192898
# Johnny Lou / 301172395
#
# Builds the File Server.

_build()
{
  javac *.java
}

_main()
{
  _build
}

_main
