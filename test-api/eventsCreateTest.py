import requests
import json
import unittest

#TEST ENDPOINTS FOR EVENT CREATE

class TestCreate(unittest.TestCase):
	global s
	global cookie
	global groupID
	s = requests.Session()

	#log in
	data = json.dumps({
		"name": "test1",
		"pass": "pass"
		})
	url = "https://scheduleit.duckdns.org/api/user/login"
	r = s.post(url, data = data)
	cookie = r.json()["cookie"]

	#create a group
	data = json.dumps({
		"cookie": cookie,
		"groupname": "test group 26"
		})
	url = "https://scheduleit.duckdns.org/api/user/groups/create"
	r = s.post(url, data = data)
	groupID = r.json()["groupid"]

	def test1(self):
		#work meeting
		data = json.dumps({
			"cookie": cookie,
			"name": "Work meeting",
			"description": "Meet with jenny at Subway",
			"type": "group.event",
			"date": "Fri, 03 Nov 2017 05:25:17 EST",
			"groupid": groupID
			})
		url = "https://scheduleit.duckdns.org/api/user/groups/calendar/add"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)

	def test2(self):
		#lunch with dad
		data = json.dumps({
			"cookie": cookie,
			"name": "Lunch with dad",
			"description": "Meet with jenny at Subway",
			"type": "group.event",
			"date": "Wed, 10 Nov 2010 05:00:00 GMT",
			"groupid": groupID
			})
		url = "https://scheduleit.duckdns.org/api/user/groups/calendar/add"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)

	def test3(self):
		#Get groceries
		data = json.dumps({
			"cookie": cookie,
			"name": "Pickup groceries",
			"description": "Go to Kroger and get groceries",
			"type": "group.event",
			"date": "Fri, 10 Nov 2017 05:00:00 GMT",
			"groupid": groupID
			})
		url = "https://scheduleit.duckdns.org/api/user/groups/calendar/add"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)

			   
if __name__ == '__main__':
	unittest.main()

