from flask_testing import TestCase
from Demo import app, db
from Demo.models import User

class BaseTestCase(TestCase):
    """A base test case."""
    def create_app(self):
        app.config.from_object('config.TestConfig')
        return app

    def setUp(self):
        db.create_all()
        user = User()
        user.username = 'steve'
        user.email = 'steve@steve.com'
        db.session.add(user)
    
    def tearDown(self):
        db.session.remove()
        db.drop_all()
