#!/bin/bash

# Installs all dependencies required for the application.

case "$OSTYPE" in
  darwin*)  
    # macOS
    brew update
    brew install mongodb
    sudo mkdir /data/db
    pip install -r requirements.txt --user
    ;; 
  linux*)   
    # ubuntu

    VAGRANT_DIRECTORY='/home/ubuntu/project/'

    sudo apt update
    sudo apt install python -y
    sudo apt install python-pip -y
    sudo apt install mongodb-server -y
    sudo mkdir /data
    sudo mkdir /data/db
    
    cd $VAGRANT_DIRECTORY
    pip install -r requirements.txt --user
    python run.py &
    ;;
  *)        
	echo "unknown: $OSTYPE" ;;
esac
