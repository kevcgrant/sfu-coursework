from Demo import db, mongo
from Demo.models import User

def create_user(username, email):
	#SQL
	user = User(username = username, email = email)
	db.session.add(user)
	db.session.commit()

	#Mongo
	mongo.db.users.insert_one({"username" : username, "email" : email})