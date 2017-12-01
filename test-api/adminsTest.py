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

	#TEST GROUP ID = 225
	groupID = 225

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
		#add user as admin
		data = json.dumps({
		  "cookie" : cookie4,
		  "groupmember" : "test1",
		  "groupid" : groupID
		})
		url = "https://scheduleit.duckdns.org/api/user/groups/admin/add"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)

		#Admin check
		data = json.dumps({
			"cookie": cookie4,
			"groupid" : groupID,
			"groupmember" : "test1"
		})
		url = "https://scheduleit.duckdns.org/api/user/groups/admin/check"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)
		isAdmin = r.json()["value"]		
		self.assertTrue(isAdmin == "true")

	def test2(self):
		#remove user as admin
		data = json.dumps({
		  "cookie" : cookie4,
		  "groupmember" : "test1",
		  "groupid" : groupID
		})
		url = "https://scheduleit.duckdns.org/api/user/groups/admin/remove"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)

		#Admin check
		data = json.dumps({
			"cookie": cookie4,
			"groupid" : groupID,
			"groupmember" : "test1"
		})
		url = "https://scheduleit.duckdns.org/api/user/groups/admin/check"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)
		isAdmin = r.json()["value"]		
		self.assertTrue(isAdmin == "false")

	def test3(self):
		#add non-group member as admin
		data = json.dumps({
		  "cookie" : cookie4,
		  "groupmember" : "Tommy1",
		  "groupid" : groupID
		})
		url = "https://scheduleit.duckdns.org/api/user/groups/admin/add"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 400)
		ret = r.json()["error"]
		length = len((str)(ret)) > 0
		hasErrorMessage = False
		if length > 0:
			hasErrorMessage = True
		self.assertTrue(hasErrorMessage)

	def test4(self):
		#remove non-admin group member
		data = json.dumps({
		  "cookie" : cookie4,
		  "groupmember" : "test1",
		  "groupid" : groupID
		})
		url = "https://scheduleit.duckdns.org/api/user/groups/admin/remove"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 400)
		ret = r.json()["error"]
		length = len((str)(ret)) > 0
		hasErrorMessage = False
		if length > 0:
			hasErrorMessage = True
		self.assertTrue(hasErrorMessage)

	def test5(self):
		#add non-group member as admin
		data = json.dumps({
		  "cookie" : cookie4,
		  "groupmember" : "Tommy1",
		  "groupid" : groupID
		})
		url = "https://scheduleit.duckdns.org/api/user/groups/admin/add"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 400)
		ret = r.json()["error"]
		length = len((str)(ret)) > 0
		hasErrorMessage = False
		if length > 0:
			hasErrorMessage = True
			self.assertTrue(hasErrorMessage)

	def test6(self):
		#user not logged in (bogus cookie)
		data = json.dumps({
		  "cookie" : 0,
		  "groupmember" : "test1",
		  "groupid" : groupID
		})
		url = "https://scheduleit.duckdns.org/api/user/groups/admin/add"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 400)
		ret = r.json()["error"]
		length = len((str)(ret)) > 0
		hasErrorMessage = False
		if length > 0:
			hasErrorMessage = True
		self.assertTrue(hasErrorMessage)

	def test7(self):
		#Group member error. User to add as admin doesn't exist
		data = json.dumps({
		  "cookie" : cookie4,
		  "groupmember" : "Tommy1",
		  "groupid" : groupID
		})
		url = "https://scheduleit.duckdns.org/api/user/groups/admin/add"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 400)
		ret = r.json()["error"]
		length = len((str)(ret)) > 0
		hasErrorMessage = False
		if length > 0:
			hasErrorMessage = True
		self.assertTrue(hasErrorMessage)

	def test8(self):
		#add user as admin
		data = json.dumps({
		  "cookie" : cookie4,
		  "groupmember" : "Willay",
		  "groupid" : groupID
		})
		url = "https://scheduleit.duckdns.org/api/user/groups/admin/add"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)

		#Admin check
		data = json.dumps({
			"cookie": cookie4,
			"groupid" : groupID,
			"groupmember" : "Willay"
		})
		url = "https://scheduleit.duckdns.org/api/user/groups/admin/check"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)
		isAdmin = r.json()["value"]		
		self.assertTrue(isAdmin == "true")

	
	def test9(self):
		#add another group member with newly appointed admin
		data = json.dumps({
		  "cookie" : cookie2,
		  "groupmember" : "test1",
		  "groupid" : groupID
		})
		url = "https://scheduleit.duckdns.org/api/user/groups/admin/add"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)

		#Admin check
		data = json.dumps({
			"cookie": cookie2,
			"groupid" : groupID,
			"groupmember" : "test1"
		})
		url = "https://scheduleit.duckdns.org/api/user/groups/admin/check"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)
		isAdmin = r.json()["value"]		
		self.assertTrue(isAdmin == "true")


	def test10(self):
		#check admins

		#should be Clarence, Willay, test1
		data = json.dumps({
			"cookie": cookie2,
			"groupid" : groupID,
			"groupmember" : "test1"
		})
		url = "https://scheduleit.duckdns.org/api/user/groups/admin/check"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)
		isAdmin = r.json()["value"]		
		self.assertTrue(isAdmin == "true")

		data = json.dumps({
			"cookie": cookie4,
			"groupid" : groupID,
			"groupmember" : "Willay"
		})
		url = "https://scheduleit.duckdns.org/api/user/groups/admin/check"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)
		isAdmin = r.json()["value"]		
		self.assertTrue(isAdmin == "true")

		data = json.dumps({
			"cookie": cookie2,
			"groupid" : groupID,
			"groupmember" : "Clarence"
		})
		url = "https://scheduleit.duckdns.org/api/user/groups/admin/check"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)
		isAdmin = r.json()["value"]		
		self.assertTrue(isAdmin == "true")



	def test11(self):
		#remove another group member
		data = json.dumps({
		  "cookie" : cookie4,
		  "groupmember" : "Willay",
		  "groupid" : groupID
		})
		url = "https://scheduleit.duckdns.org/api/user/groups/admin/remove"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)

		#Admin check
		data = json.dumps({
			"cookie": cookie4,
			"groupid" : groupID,
			"groupmember" : "test1"
		})
		url = "https://scheduleit.duckdns.org/api/user/groups/admin/check"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)
		isAdmin = r.json()["value"]		
		self.assertTrue(isAdmin == "false")


	def test12(self):
		#remove another group member
		data = json.dumps({
		  "cookie" : cookie4,
		  "groupmember" : "test1",
		  "groupid" : groupID
		})
		url = "https://scheduleit.duckdns.org/api/user/groups/admin/remove"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)

		#Admin check
		data = json.dumps({
			"cookie": cookie4,
			"groupid" : groupID,
			"groupmember" : "test1"
		})
		url = "https://scheduleit.duckdns.org/api/user/groups/admin/check"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)
		isAdmin = r.json()["value"]		
		self.assertTrue(isAdmin == "false")

	def test13(self):
		#get admins
		data = json.dumps({
			"cookie": cookie2,
			"groupid" : groupID,
			"groupmember" : "Clarence"
		})
		url = "https://scheduleit.duckdns.org/api/user/groups/admin/check"
		r = s.post(url, data = data)
		self.assertTrue(r.status_code == 200)
		isAdmin = r.json()["value"]		
		self.assertTrue(isAdmin == "true")

if __name__ == '__main__':
	unittest.main()

