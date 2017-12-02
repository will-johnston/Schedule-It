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
		"name": "u1",
		"pass": "pass"
		})
	url = "https://scheduleit.duckdns.org/api/user/login"
	r = s.post(url, data = data)
	cookie = r.json()["cookie"]
	print r

	def test1(self):
		#create an event
		data = json.dumps({
			"cookie": cookie,
			"name": "event 1",
			"description": "desc",
			"type": "group.event",
			"date": "Fri, 01 Dec 2017 01:07:00 EST",
			"groupid": "215",
			"expiration_time": "None"
			})
		url = "https://scheduleit.duckdns.org/api/user/groups/calendar/add"
		r = s.post(url, data = data)
		print r

		#call check
		data = json.dumps({
			"cookie": cookie,
			"groupid": "215"
			})
		url = "https://scheduleit.duckdns.org/api/user/groups/calendar/check"
		r = s.post(url, data = data)
		print r

		#get notifications
		data = json.dumps({
			"cookie": cookie
			})
		url = "https://scheduleit.duckdns.org/api/user/notifications/get"
		r = s.post(url, data = data)
		print r

		found = False
		for obj in r.json():
			if(obj["type"] == "remind.event"):
				if(obj["name"] == "event 1"):
					found = True

		self.assertTrue(found)

	def test2(self):
		#create an event
		data = json.dumps({
			"cookie": cookie,
			"name": "event 2",
			"description": "desc",
			"type": "group.event",
			"date": "Fri, 01 Dec 2017 02:07:00 EST",
			"groupid": "215",
			"expiration_time": "None"
			})
		url = "https://scheduleit.duckdns.org/api/user/groups/calendar/add"
		r = s.post(url, data = data)
		print r

		#call check
		data = json.dumps({
			"cookie": cookie,
			"groupid": "215"
			})
		url = "https://scheduleit.duckdns.org/api/user/groups/calendar/check"
		r = s.post(url, data = data)
		print r

		#get notifications
		data = json.dumps({
			"cookie": cookie
			})
		url = "https://scheduleit.duckdns.org/api/user/notifications/get"
		r = s.post(url, data = data)
		print r

		found = False
		for obj in r.json():
			if(obj["type"] == "remind.event"):
				if(obj["name"] == "event 2"):
					found = True

		self.assertTrue(found)

	def test3(self):
		#create an event
		data = json.dumps({
			"cookie": cookie,
			"name": "event 3",
			"description": "desc",
			"type": "group.event",
			"date": "Fri, 01 Dec 2017 03:07:00 EST",
			"groupid": "215",
			"expiration_time": "None"
			})
		url = "https://scheduleit.duckdns.org/api/user/groups/calendar/add"
		r = s.post(url, data = data)
		print r

		#call check
		data = json.dumps({
			"cookie": cookie,
			"groupid": "215"
			})
		url = "https://scheduleit.duckdns.org/api/user/groups/calendar/check"
		r = s.post(url, data = data)
		print r

		#get notifications
		data = json.dumps({
			"cookie": cookie
			})
		url = "https://scheduleit.duckdns.org/api/user/notifications/get"
		r = s.post(url, data = data)
		print r

		found = False
		for obj in r.json():
			if(obj["type"] == "remind.event"):
				if(obj["name"] == "event 3"):
					found = True

		self.assertTrue(found)


			   
if __name__ == '__main__':
	unittest.main()

