import unittest
from baset import BaseTestCase

class FlaskTestCase(BaseTestCase):

	def test_home(self):
			response = self.client.get('/home', content_type='html/text')
			self.assertEqual(response.status_code, 200)
		
	def test_debugger(self):
			response = self.client.post('/debugger', content_type='html/text')
			self.assertEqual(response.status_code, 200)

	def test_login(self):
			response = self.client.post('/login', content_type='html/text')
			self.assertEqual(response.status_code, 200)			

	def test_usernew(self):
			response = self.client.post('/usernew', content_type='html/text')
			self.assertEqual(response.status_code, 200)	
			
if __name__ == '__main__':
        unittest.main()