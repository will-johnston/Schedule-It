import requests
import json
import unittest

#TEST ENDPOINTS FOR ADMINS

class TestCreate(unittest.TestCase):
	global s
	global cookie1
	global cookie2
	global cookie3
	global cookie4
	global groupID
	s = requests.Session()

	#TEST GROUP ID = 225, EVENT ID = 219
	groupID = 225
	EVENT ID =  219

	#log in as user test1 --group member
	data = json.dumps({
		"name": "test1",
		"pass": "pass"
		})
	url = "https://scheduleit.duckdns.org/api/user/login"
	r = s.post(url, data = data)
	cookie1 = r.json()["cookie"]

	#login as user Willay  --group member
	data = json.dumps({
		"name": "Willay",
		"pass": "Willard1"
		})
	url = "https://scheduleit.duckdns.org/api/user/login"
	r = s.post(url, data = data)
	cookie2 = r.json()["cookie"]

	#login as user Tommy  --not a group member
	data = json.dumps({
		"name": "Tommy1",
		"pass": "Tom1"
		})
	url = "https://scheduleit.duckdns.org/api/user/login"
	r = s.post(url, data = data)
	cookie3 = r.json()["cookie"]

	#login as clarence (admin)  --creator
	data = json.dumps({
		"name": "Clarence",
		"pass": "roboto"
		})
	url = "https://scheduleit.duckdns.org/api/user/login"
	r = s.post(url, data = data)
	cookie4 = r.json()["cookie"]


	def test1(self):
		#add time input as clarence
		data = json.dumps({"groupID": groupID, "eventID": eventID, "time" : "Tue, 05 December 2017 at 7:00:00 pm"})
		url = "https://scheduleit.duckdns.org/api/timeinput/add"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)

	def test2(self):
		#add another time input
		data = json.dumps({"groupID": groupID, "eventID": eventID, "time" : "Tue, 05 December 2017 at 6:30:00 pm"})
		url = "https://scheduleit.duckdns.org/api/timeinput/add"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)

	def test3(self):
		#add incorrect time input
		data = json.dumps({"groupID": groupID, "eventID": eventID, "time" : "Fri, 05 December 2017 at 7 pm"})
		url = "https://scheduleit.duckdns.org/api/timeinput/add"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 400)

	def test4(self):
		#add time input no event exists
		data = json.dumps({"groupID": groupID, "eventID": -29, "time" : "Wed, 05 December 2017 at 7 pm"})
		url = "https://scheduleit.duckdns.org/api/timeinput/add"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)

	def test5(self):
		#add time input
		data = json.dumps({"groupID": groupID, "eventID": eventID, "time" : "Mon, 04 December 2017 at 4 pm"})
		url = "https://scheduleit.duckdns.org/api/timeinput/add"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)
	
	def test6(self):
		#add time input
		data = json.dumps({"groupID": groupID, "eventID": eventID, "time" : "Mon, 04 December 2017 at 7 am"})
		url = "https://scheduleit.duckdns.org/api/timeinput/add"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)

	def test7(self):
		#add time input outlier
		data = json.dumps({"groupID": groupID, "eventID": eventID, "time" : "Tue, 04 December 2018 at 4 pm"})
		url = "https://scheduleit.duckdns.org/api/timeinput/add"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)

	def test8(self):
		#find best time not group member
		data = json.dumps({
		  "cookie" : cookie3,
		  "groupid" : groupID,
		  "eventid" : eventID
		})
		url = "https://scheduleit.duckdns.org/api/findbesttime"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 400)
		ret = r.json()["error"]
		length = len((str)(ret)) > 0
		hasErrorMessage = False
		if length > 0:
			hasErrorMessage = True
		self.assertTrue(hasErrorMessage)

	def test9(self):
		#find best time not admin
		data = json.dumps({
		  "cookie" : cookie1,
		  "groupid" : groupID,
		  "eventid" : eventID
		})
		url = "https://scheduleit.duckdns.org/api/findbesttime"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 400)
		ret = r.json()["error"]
		length = len((str)(ret)) > 0
		hasErrorMessage = False
		if length > 0:
			hasErrorMessage = True
		self.assertTrue(hasErrorMessage)

	def test10(self):
		#find best time not admin
		data = json.dumps({
		  "cookie" : cookie2,
		  "groupid" : groupID,
		  "eventid" : eventID
		})
		url = "https://scheduleit.duckdns.org/api/findbesttime"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 400)
		ret = r.json()["error"]
		length = len((str)(ret)) > 0
		hasErrorMessage = False
		if length > 0:
			hasErrorMessage = True
		self.assertTrue(hasErrorMessage)

	def test11(self):
		#find best time not open-ended ('event 1' from group with id = 215)
		data = json.dumps({
		  "cookie" : cookie4,
		  "groupid" : 215,
		  "eventid" : 149
		})
		url = "https://scheduleit.duckdns.org/api/findbesttime"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 400)
		ret = r.json()["error"]
		length = len((str)(ret)) > 0
		hasErrorMessage = False
		if length > 0:
			hasErrorMessage = True
		self.assertTrue(hasErrorMessage)

	def test12(self):
		#find best time not a group
		data = json.dumps({
		  "cookie" : cookie2,
		  "groupid" : -33,
		  "eventid" : eventID
		})
		url = "https://scheduleit.duckdns.org/api/findbesttime"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 400)
		ret = r.json()["error"]
		length = len((str)(ret)) > 0
		hasErrorMessage = False
		if length > 0:
			hasErrorMessage = True
		self.assertTrue(hasErrorMessage)

	def test13(self):
		#find best time cannot get user
		data = json.dumps({
		  "cookie" : 0,
		  "groupid" : groupID,
		  "eventid" : eventID
		})
		url = "https://scheduleit.duckdns.org/api/findbesttime"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 400)
		ret = r.json()["error"]
		length = len((str)(ret)) > 0
		hasErrorMessage = False
		if length > 0:
			hasErrorMessage = True
		self.assertTrue(hasErrorMessage)

	def test14(self):
		#find best time valid request
		data = json.dumps({
		  "cookie" : cookie4,
		  "groupid" : groupID,
		  "eventid" : eventID
		})
		url = "https://scheduleit.duckdns.org/api/findbesttime"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)
		ret = r.json()["error"]
		length = len((str)(ret)) > 0
		hasErrorMessage = False
		if length > 0:
			hasErrorMessage = True
		self.assertTrue(hasErrorMessage)


if __name__ == '__main__':
	unittest.main()

