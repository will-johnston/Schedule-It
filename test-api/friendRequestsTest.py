import requests
import json
import unittest

#TEST ENDPOINTS FOR FRIENDS

class TestCreate(unittest.TestCase):
  global s
  s = requests.Session()

  def test1(self):
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

  def test2(self):
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


  def test3(self):
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
    self.assertEquals(200, code)


  def test4(self):
    #tests for samantha added in friends list
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
    #shows friends
    url = "https://scheduleit.duckdns.org/api/user/friends/get"
    r = s.post(url, data=data)
    friend = r.json()["friends"][0]
    self.assertEquals(friend, "Samantha89")


  def test5(self):
    #delete friend

    data = json.dumps({  
      "pass" : "nunchucks7", 
      "name" : "Samantha89" 
    })
    url = "https://scheduleit.duckdns.org/api/user/login"
    r = s.post(url, data=data)
    cookie = r.json()["cookie"]

    data = json.dumps({
      "cookie" : str(cookie),
      "username" : "Dee"
    })
    url = "https://scheduleit.duckdns.org/api/user/friends/remove"
    r = s.post(url, data=data)
    self.assertEquals(200, r.status_code)

  
  def test6(self):
    #extra delete friend (should be bad request)

    data = json.dumps({  
      "pass" : "nunchucks7", 
      "name" : "Samantha89" 
    })
    url = "https://scheduleit.duckdns.org/api/user/login"
    r = s.post(url, data=data)
    cookie = r.json()["cookie"]

    data = json.dumps({
      "cookie" : str(cookie),
      "username" : "Dee"
    })
    url = "https://scheduleit.duckdns.org/api/user/friends/remove"
    r = s.post(url, data=data)
    self.assertEquals(400, r.status_code)

  def test7(self):
    #extra delete friend (should be bad request)

    data = json.dumps({  
      "pass" : "nunchucks7", 
      "name" : "Samantha89" 
    })
    url = "https://scheduleit.duckdns.org/api/user/login"
    r = s.post(url, data=data)
    cookie = r.json()["cookie"]

    data = json.dumps({
      "cookie" : str(cookie),
      "username" : "Dee"
    })
    url = "https://scheduleit.duckdns.org/api/user/friends/remove"
    r = s.post(url, data=data)
    self.assertEquals(400, r.status_code)

  def test8(self):
    #bad invite user
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
      "username" : "asdl;kfjasldkfjasdl;kfj%$@%@$^$%#&^EDSGSFfffffa" 
    })
    url = "https://scheduleit.duckdns.org/api/user/friends/invite"
    r = s.post(url, data=data)
    ret = r.status_code

    self.assertEquals(400, ret)

  def test9(self):
    #bad invite cookie
    data = json.dumps({  
      "pass" : "nunchucks7", 
      "name" : "Samantha89" 
    })
    url = "https://scheduleit.duckdns.org/api/user/login"
    r = s.post(url, data=data)
    cookie = r.json()["cookie"]

    #invite
    data = json.dumps({  
      "cookie" : "nan", 
      "username" : "Dee" 
    })
    url = "https://scheduleit.duckdns.org/api/user/friends/invite"
    r = s.post(url, data=data)
    ret = r.status_code

    self.assertEquals(400, ret)




if __name__ == '__main__':
    unittest.main()


  