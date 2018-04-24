import re

CLASS_REGEX = re.compile(' class="(.+?)"')

class Block:
	def __init__(self, file, code):
		self.file = file
		self.code = code


def __getFileFromPath(filename):
	return filename[filename.rfind('/')+1:]

"""
Reads lines from the file starting from the line that contains the specified string,
up until a blank line is found.
Strips class attributes from tags to clean it up a bit.
"""
def __readLinesUntilBlank(substring, filename, ignore=None):
	code = ""
	foundString = False
	with open(filename) as file:
		for line in file:
			if foundString:
				if ignore and ignore in line:
					continue
				elif line.strip():
					code += CLASS_REGEX.sub("", line)
				else:
					break
			else:
				if substring in line:
					foundString = True
					code = CLASS_REGEX.sub("", line);

	actualFile = __getFileFromPath(filename)
	return Block(code = code, file = actualFile)


"""
Reads the function for the specified route, ignoring lines for displaying blocks.
"""
def __readRoute(route):
	return __readLinesUntilBlank(route, "Demo/views.py", "blocks")

"""
Reads an entire file.
"""
def __readWholeFile(filename):
	with open(filename) as file:
		return Block(code = file.read(), file = __getFileFromPath(filename))


TEMPLATES_DIR = "Demo/templates/"
NEW_USER_TEMPLATE = __readLinesUntilBlank("form", TEMPLATES_DIR + "user_new.html")
SQL_TABLE = __readLinesUntilBlank("tbody", TEMPLATES_DIR + "databases.html")
MONGO_LIST = __readLinesUntilBlank("<ul", TEMPLATES_DIR + "databases.html")
DATABASES_ROUTE = __readRoute("/databases")
NEW_USER_ROUTE = __readRoute("/usernew")
DATABASES_INIT = __readLinesUntilBlank("from_object", "Demo/__init__.py")
USER_MODEL = __readLinesUntilBlank("User", "Demo/models.py")
LOGOUT = __readLinesUntilBlank("session.pop", "Demo/views.py")
LOGIN = __readLinesUntilBlank("session[", "Demo/views.py")