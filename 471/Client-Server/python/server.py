#-----------------------------
#  CMPT 371 Coding Project 1 -
#  file: server.py           -
#  author: Kevin Grant       -
#          301192898         -
#-----------------------------

#import time to display date and time header line
import time

#import socket module
from socket import *
serverSocket = socket(AF_INET, SOCK_STREAM)

serverPort = 1358

#Prepare a sever socket
serverSocket.bind(('', serverPort))
serverSocket.listen(1)

while True:
    
    #Establish the connection
    print 'Ready to serve...'
    connectionSocket, addr = serverSocket.accept()
    try:
        message = connectionSocket.recv(4096)      
        filename = message.split()[1]

        f = open(filename[1:])
        outputdata = f.read()
        
        #Send one HTTP header line into socket
        connectionSocket.send('HTTP/1.1 200 OK\r\n')
        #Sends the current time header line to the socket
        connectionSocket.send(time.strftime('%a, %d %b %Y %H:%M:%S %Z\r\n\r\n'))
        
        #if GET request, send the content of the requested file to the client
        if message.split()[0] == 'GET':
            #For loop iterates through the data in the file
            for i in range(0, len(outputdata)):
                connectionSocket.send(outputdata[i])
            #Close the socket after data has all been sent
            connectionSocket.close()

        #if HEAD request, send TRUE response if file exists.
        if message.split()[0] == 'HEAD':
            connectionSocket.send('TRUE')
            connectionSocket.close()
        
    except IOError:
        #Send response message for file not found for both HEAD and GET requests
        error = "404 Not Found"
        connectionSocket.send(error)
        #Close client socket
        connectionSocket.close()

serverSocket.close()
