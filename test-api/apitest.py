import requests
import json

#all cookies received will be stored in the session object

#import requests
#r = requests.post("https://scheduleit.duckdns.org/api/user/login", json={"name": "will", "pass": "pass"})
#print r.status_code
#print r.json



#get notifs
s = requests.Session()
groupID = 104
userID = 96
data = json.dumps({"groupID": str(groupID), "userID": str(userID)})
url = "https://scheduleit.duckdns.org/api/ugejunction/get"
r = s.post(url, data=data)
eventID = r.json()["eventID"]
print eventID
if (int(eventID) > 0):
	print "ok"

'''
s = requests.Session()
data = json.dumps({
  'text': '<63> <53> schedule me for January 3rd at 7 pm'
})
url = "http://willjohnston.pythonanywhere.com/api/chatterbot/"
r = s.post(url, data=data)
response = r.json()["text"]
print response


#data = {"name": "will", "pass": "pass"}
data = json.dumps({"name": "Clarence", "pass": "roboto"})
url = "https://scheduleit.duckdns.org/api/user/login"
r = s.post(url, data=data)
cookie = r.text


url = "https://scheduleit.duckdns.org/api/user/groups/get"
r = s.post(url, data=cookie)
#print r.text

print r.json()

print r.json()[0]["name"]

groupID = 27
eventID = 1
userID = 16


data = json.dumps({"groupID": str(groupID), "eventID": str(eventID), "time" : "2017-12-11 19:00:00"})
url = "https://scheduleit.duckdns.org/api/timeinput/add"
r = s.post(url, data=data)
print r


data = json.dumps({"groupID": str(groupID), "userID": str(userID)})
url = "https://scheduleit.duckdns.org/api/ugejunction/get"
r = s.post(url, data=data)
print r.json()["eventID"]
#	eventID = r.json()["eventID"]
#s.post('scheduleit.duckdns.org/user/login',data=payload)
#s.get('http://www...')
'''
