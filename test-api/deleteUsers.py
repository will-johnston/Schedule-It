import requests
import json

#Delete Samantha
s = requests.Session()
data = json.dumps({
  "name" : "Samantha89",
  "pass" : "nunchucks7"
})
url = "https://scheduleit.duckdns.org/api/user/login"
r = s.post(url, data=data)
cookie = r.json()["cookie"]

s = requests.Session()
data = json.dumps({
  "cookie" : str(cookie)
})
url = "https://scheduleit.duckdns.org/api/user/getId"
r = s.post(url, data=data)
userID = r.json()

data = json.dumps({
  "userid" : str(userID)
})
url = "https://scheduleit.duckdns.org/api/test/user/delete"
r = s.post(url, data=data)
print r.status_code



#Delete Charles
s = requests.Session()
data = json.dumps({
  "name" : "charles_barkley",
  "pass" : "gophertoker"
})
url = "https://scheduleit.duckdns.org/api/user/login"
r = s.post(url, data=data)
cookie = r.json()["cookie"]

s = requests.Session()
data = json.dumps({
  "cookie" : str(cookie)
})
url = "https://scheduleit.duckdns.org/api/user/getId"
r = s.post(url, data=data)
userID = r.json()

data = json.dumps({
  "userid" : str(userID)
})
url = "https://scheduleit.duckdns.org/api/test/user/delete"
r = s.post(url, data=data)
print r.status_code



#Delete Casey
s = requests.Session()
data = json.dumps({
  "name" : "Cut_to_the_casey",
  "pass" : "absolutelynot"
})
url = "https://scheduleit.duckdns.org/api/user/login"
r = s.post(url, data=data)
cookie = r.json()["cookie"]

s = requests.Session()
data = json.dumps({
  "cookie" : str(cookie)
})
url = "https://scheduleit.duckdns.org/api/user/getId"
r = s.post(url, data=data)
userID = r.json()

data = json.dumps({
  "userid" : str(userID)
})
url = "https://scheduleit.duckdns.org/api/test/user/delete"
r = s.post(url, data=data)
print r.status_code



#Delete Tyler
s = requests.Session()
data = json.dumps({
  "name" : "Tylerrrrr",
  "pass" : "sherlocked"
})
url = "https://scheduleit.duckdns.org/api/user/login"
r = s.post(url, data=data)
cookie = r.json()["cookie"]

s = requests.Session()
data = json.dumps({
  "cookie" : str(cookie)
})
url = "https://scheduleit.duckdns.org/api/user/getId"
r = s.post(url, data=data)
userID = r.json()

data = json.dumps({
  "userid" : str(userID)
})
url = "https://scheduleit.duckdns.org/api/test/user/delete"
r = s.post(url, data=data)
print r.status_code



#Delete Dee
s = requests.Session()
data = json.dumps({
  "name" : "Dee",
  "pass" : "sherlocked"
})
url = "https://scheduleit.duckdns.org/api/user/login"
r = s.post(url, data=data)
cookie = r.json()["cookie"]

s = requests.Session()
data = json.dumps({
  "cookie" : str(cookie)
})
url = "https://scheduleit.duckdns.org/api/user/getId"
r = s.post(url, data=data)
userID = r.json()

data = json.dumps({
  "userid" : str(userID)
})
url = "https://scheduleit.duckdns.org/api/test/user/delete"
r = s.post(url, data=data)
print r.status_code



#Delete Patricia
s = requests.Session()
data = json.dumps({
  "name" : "pattycakes",
  "pass" : "sherlocked"
})
url = "https://scheduleit.duckdns.org/api/user/login"
r = s.post(url, data=data)
cookie = r.json()["cookie"]

s = requests.Session()
data = json.dumps({
  "cookie" : str(cookie)
})
url = "https://scheduleit.duckdns.org/api/user/getId"
r = s.post(url, data=data)
userID = r.json()

data = json.dumps({
  "userid" : str(userID)
})
url = "https://scheduleit.duckdns.org/api/test/user/delete"
r = s.post(url, data=data)
print r.status_code


