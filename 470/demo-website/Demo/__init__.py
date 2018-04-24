from config import Config
from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_pymongo import PyMongo

app = Flask(__name__)
wsgi_app = app.wsgi_app #Registering with IIS

app.config.from_object(Config)
app.secret_key = '\xb8ir\xdfs\x96\x9b\x9e#fr\x91%\xa8@\xb7\x0ce}\xfbv\xb6\xb9I'
mongo = PyMongo(app)
db = SQLAlchemy(app)
import Demo.models
db.create_all()

import Demo.views
import Demo.blocks
