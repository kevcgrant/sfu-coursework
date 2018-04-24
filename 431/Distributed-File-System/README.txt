CMPT 431 ASSIGNMENT 5
Kevin Grant / 301192898
Johnny Lou / 301172395

Server written and compiled with Java 8.

All classes, build.sh, and run.sh are in the src folder.

To gracefully exit the server, send SIGINT (ctrl+C)
To kill the server: $ kill -9 <server_pid>

To run via script (from the src folder):
$ ./build.sh
$ ./run.sh 127.0.0.1 12345 ./tmp/

To run manually (from the src folder):
$ ./build.sh
$ java Server 127.0.0.1 12345 ./tmp/
