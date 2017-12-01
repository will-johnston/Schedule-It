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
			"date": "Fri, 01 Dec 2017 03:20:00 EST",
			"groupid": "218",
			"expiration_time": "None"
			})
		url = "https://scheduleit.duckdns.org/api/user/groups/calendar/add"
		r = s.post(url, data = data)
		print r

		#get notifications
		data = json.dumps({
			"cookie": cookie
			})
		url = "https://scheduleit.duckdns.org/api/user/notifications/get"
		r = s.post(url, data = data)
		print r

		eventID = 0;
		for obj in r.json():
			if(obj["type"] == "invite.event"):
				if(obj["name"] == "event 1"):
					eventID = obj["id"]

		#respond to notification
		data = json.dumps({
			"cookie": cookie,
			"notification" : {
				"id" : eventID,
				"type" : "event.invite"
			},
			"response" : {
				"accept" : "going"
			}
			})
		url = "https://scheduleit.duckdns.org/api/user/notifications/respond"
		r = s.post(url, data = data)
		print r
		self.assertTrue(r.status_code == 200)

	def test2(self):
		#create an event
		data = json.dumps({
			"cookie": cookie,
			"name": "event 2",
			"description": "desc",
			"type": "group.event",
			"date": "Fri, 01 Dec 2017 04:20:00 EST",
			"groupid": "218",
			"expiration_time": "None"
			})
		url = "https://scheduleit.duckdns.org/api/user/groups/calendar/add"
		r = s.post(url, data = data)
		print r

		#get notifications
		data = json.dumps({
			"cookie": cookie
			})
		url = "https://scheduleit.duckdns.org/api/user/notifications/get"
		r = s.post(url, data = data)
		print r

		eventID = 0;
		for obj in r.json():
			if(obj["type"] == "invite.event"):
				if(obj["name"] == "event 2"):
					eventID = obj["id"]

		#respond to notification
		data = json.dumps({
			"cookie": cookie,
			"notification" : {
				"id" : eventID,
				"type" : "event.invite"
			},
			"response" : {
				"accept" : "going"
			}
			})
		url = "https://scheduleit.duckdns.org/api/user/notifications/respond"
		r = s.post(url, data = data)
		print r
		self.assertTrue(r.status_code == 200)

	def test3(self):
		#create an event
		data = json.dumps({
			"cookie": cookie,
			"name": "event 3",
			"description": "desc",
			"type": "group.event",
			"date": "Fri, 01 Dec 2017 05:20:00 EST",
			"groupid": "218",
			"expiration_time": "None"
			})
		url = "https://scheduleit.duckdns.org/api/user/groups/calendar/add"
		r = s.post(url, data = data)
		print r

		#get notifications
		data = json.dumps({
			"cookie": cookie
			})
		url = "https://scheduleit.duckdns.org/api/user/notifications/get"
		r = s.post(url, data = data)
		print r

		eventID = 0;
		for obj in r.json():
			if(obj["type"] == "invite.event"):
				if(obj["name"] == "event 3"):
					eventID = obj["id"]

		#respond to notification
		data = json.dumps({
			"cookie": cookie,
			"notification" : {
				"id" : eventID,
				"type" : "event.invite"
			},
			"response" : {
				"accept" : "going"
			}
			})
		url = "https://scheduleit.duckdns.org/api/user/notifications/respond"
		r = s.post(url, data = data)
		print r
		self.assertTrue(r.status_code == 200)


			   
if __name__ == '__main__':
	unittest.main()

