import requests
import json
import unittest

#TEST ENDPOINTS FOR DELETE USER

class TestCreate(unittest.TestCase):
  global s
  s = requests.Session()

  def invite_response_test(self):
    #login
    data = json.dumps({  
      "pass" : "nunchucks7", 
      "name" : "Samantha89" 
    })
    url = "https://scheduleit.duckdns.org/api/user/login"
    r = s.post(url, data=data)
    cookie = r.json()["cookie"]

    #invite
    data = json.dumps({  
      "cookie" : str(cookie), 
      "username" : "Dee" 
    })
    url = "https://scheduleit.duckdns.org/api/user/friends/invite"
    r = s.post(url, data=data)
    ret = r.status_code
    self.assertEquals(200, ret)

  def notifications_get(self):
    #login as Dee
    data = json.dumps({
      "pass" : "sherlocked",
      "name" : "Dee"
    })
    url = "https://scheduleit.duckdns.org/api/user/login"
    r = s.post(url, data=data)
    cookie = r.json()["cookie"]


    #get notifs
    data = json.dumps({
      "cookie" : str(cookie)
    })
    url = "https://scheduleit.duckdns.org/api/user/notifications/get"
    r = s.post(url, data=data)
    type = r.json()[0]["type"]
    id = r.json()[0]["id"]
    self.assertTrue(id > 0)
    self.assertEquals(type, "invite.friend")


  def notifications_respond(self):
    #login as Dee
    data = json.dumps({
      "pass" : "sherlocked",
      "name" : "Dee"
    })
    url = "https://scheduleit.duckdns.org/api/user/login"
    r = s.post(url, data=data)
    cookie = r.json()["cookie"]


    #get notifs
    data = json.dumps({
      "cookie" : str(cookie)
    })
    url = "https://scheduleit.duckdns.org/api/user/notifications/get"
    r = s.post(url, data=data)
    type = r.json()[0]["type"]
    id = r.json()[0]["id"]

    #respond
    data = json.dumps({
      "cookie" : str(cookie),
      "notification" : {
        "id" : str(id),
        "type" : str(type)
      },
      "response" : {
        "accept" : "true"
      }
    })
    url = "https://scheduleit.duckdns.org/api/user/notifications/respond"
    r = s.post(url, data=data)
    code = r.status_code
    assertTrue(200, code)


  def get_friends(self):

    #login as Dee
    data = json.dumps({
      "pass" : "sherlocked",
      "name" : "Dee"
    })
    url = "https://scheduleit.duckdns.org/api/user/login"
    r = s.post(url, data=data)
    cookie = r.json()["cookie"]

    data = json.dumps({
      "cookie" : str(cookie)
    })
    url = "https://scheduleit.duckdns.org/api/user/friends/get"
    r = s.post(url, data=data)
    friend = r.json()["friends"][0]
    assertTrue(friend, "Samantha89")
    

  #Dee
  def test5(self):
    data = json.dumps({
      "pass" : "sherlocked",
      "name" : "Dee"
    })
    url = "https://scheduleit.duckdns.org/api/user/login"
    r = s.post(url, data=data)
    ret = r.json()["cookie"]
    length = len((str)(ret)) > 0
    hasCookie = False
    if length > 0:
      hasCookie = True
    self.assertTrue(hasCookie)

  def test6(self):
    #Patricia
    data = json.dumps({
      "pass" : "sherlocked",
      "name" : "pattycakes"
    })
    url = "https://scheduleit.duckdns.org/api/user/login"
    r = s.post(url, data=data)
    ret = r.json()["cookie"]
    length = len((str)(ret)) > 0
    hasCookie = False
    if length > 0:
      hasCookie = True
    self.assertTrue(hasCookie)

  
  def test7(self):
    #Invalid arguments
    data = json.dumps({
      "pass" : "inv",
      "username" : "pattycakes"
    })
    url = "https://scheduleit.duckdns.org/api/user/login"
    r = s.post(url, data=data)
    ret = r.json()["error"]
    length = len((str)(ret)) > 0
    hasErrorMessage = False
    if length > 0:
      hasErrorMessage = True
    self.assertTrue(hasErrorMessage)

  def test8(self):
    #Invalid url
    data = json.dumps({
      "email" : "invalid@hotmail.com",
      "pass" : "inv",
      "name" : "Patricia Jones",
      "phone" : "575-578-0001",
      "username" : "newPat"
    })

    url = "https://scheduleit.duckdns.org/api/login"
    r = s.post(url, data=data)
    ret = r.status_code
    self.assertEquals(400, ret)
               
if __name__ == '__main__':
    unittest.main()

