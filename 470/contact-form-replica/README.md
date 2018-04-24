# contact-form-replica

CMPT 470 Exercise 3; Team Red

## Note for Markers

The setting up of the project (initial work done on HTML and CGI files) was done by all three group members, despite being committed by one person.

## Getting Started 

These are notes for our team, not the markers, on how to easily start running a server and testing.

### Prerequisites

Ensure you have python3 installed. To see what version of python3 you have installed, run the command:

```
python3 -V
```

### Installing and Testing

Clone the repository to your local machine (ensure you have already added your SSH key to your GitLab account):

```
git clone git@csil-git1.cs.surrey.sfu.ca:kcgrant/contact-form-replica.git
```

To run a simple HTTP server with CGI enabled on port 8000, change to the repository directory and start a python3 http server:
```
cd /path/to/contact-form-replica
python3 -m http.server --bind localhost --cgi 8000
```

You can then enter the URL `http://127.0.0.1:8000/contact.html` in your browser to access the web form, and submitting the data will execute the CGI script `process.py`.



