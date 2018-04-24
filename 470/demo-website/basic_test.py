#basic_test

from Demo import app
import unittest

class FlaskTestCase(unittest.TestCase):

	def test_home(self):
		tester = app.test_client(self)
		response = tester.get('/', content_type='html/text')
		self.assertEqual(response.status_code, 200)

	def test_db(self):
		tester = app.test_client(self)
		response = tester.get('/databases', content_type='html/text')
		self.assertEqual(response.status_code, 200)

	def test_login(self):
		tester = app.test_client(self)
		response = tester.get('/login', content_type='html/text')
		self.assertEqual(response.status_code, 200)

	def test_debugger(self):
		tester = app.test_client(self)
		response = tester.get('/debugger', content_type='html/text')
		self.assertEqual(response.status_code, 200)
				
if __name__ == '__main__':
		unittest.main()