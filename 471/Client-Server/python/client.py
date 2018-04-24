#-----------------------------
#  CMPT 371 Coding Project 1 -
#  file: client.py           -
#  author: Kevin Grant       -
#          301192898         -
#-----------------------------

import sys

#import socket module
from socket import *

#check if correct number of command line arguments were passed
#If not, inform the user and display the correct usage
if len(sys.argv) != 5:
    print "Invalid number of arguments. \n"
    print "Usage: \n"
    print "client.py <server_host> <server_port> <filename> <method> \n"
    sys.exit()

#check if the method type inputted is GET or HEAD
#If not, inform the user and display the correct method types
if sys.argv[4] == "HEAD":
    pass
elif sys.argv[4] == "GET":
    pass
else:
    print "Invalid method type. Must be of type 'GET' or 'HEAD'\n"
    sys.exit()

#Set variables for the corresponding argument values
serverName = sys.argv[1]
serverPort = sys.argv[2]
filename = sys.argv[3]
method = sys.argv[4]

#Create TCP Socket
clientSocket = socket(AF_INET, SOCK_STREAM)

#Connect to the server 
clientSocket.connect((serverName, int(serverPort)))

#Create the HTTP request message and send it to the server
request = method + " /" + filename + " HTTP/1.1"
clientSocket.send(str(request))

#Receive and display the response from the server
display = ""
while True:
    response = clientSocket.recv(4096)
    
    #If the server is sending no data, break
    if not response:
        break

    #Add the data to the message to dislay
    display += response

#display the data
print display

#Close the socket
clientSocket.close()
