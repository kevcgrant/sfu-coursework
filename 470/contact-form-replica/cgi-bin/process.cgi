#!/usr/local/bin/python

import cgi

RESPONSE_HTML_FILE = "process_response.txt"

form = cgi.FieldStorage()
userid = form.getvalue('userid')
subject = form.getvalue('subject')
body = form.getvalue('body')
email = form.getvalue('email')

response_file = open(RESPONSE_HTML_FILE, 'r')
response = response_file.read() % (userid, subject, body, email, userid, subject, body, email)
print(response)


