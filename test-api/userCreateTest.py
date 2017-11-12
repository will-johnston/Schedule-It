import requests
import json
import unittest

#TEST ENDPOINTS FOR CREATE USER

class TestCreate(unittest.TestCase):
  global s
  s = requests.Session()

  def test1(self):
    #Samantha
    data = json.dumps({ 
      "email" : "samantha@gmail.com", 
      "pass" : "nunchucks7", 
      "name" : "Samantha Stallings", 
      "phone" : "523-555-6766", 
      "username" : "Samantha89" 
    })
    url = "https://scheduleit.duckdns.org/api/user/create"
    r = s.post(url, data=data)
    ret = r.json()["cookie"]
    length = len((str)(ret)) > 0
    hasCookie = False
    if length > 0:
      hasCookie = True
    self.assertTrue(hasCookie)

  def test2(self):
    #Charles
    data = json.dumps({
      "email" : "charles@gmail.com",
      "pass" : "gophertoker",
      "name" : "Charlie Butch",
      "phone" : "123-555-5555",
      "username" : "charles_barkley"
    })
    url = "https://scheduleit.duckdns.org/api/user/create"
    r = s.post(url, data=data)
    ret = r.json()["cookie"]
    length = len((str)(ret)) > 0
    hasCookie = False
    if length > 0:
      hasCookie = True
    self.assertTrue(hasCookie)


  def test3(self):
    #Casey
    data = json.dumps({
      "email" : "casey@gmail.com",
      "pass" : "absolutelynot",
      "name" : "Casey Cameron",
      "phone" : "897-436-7468",
      "username" : "Cut_to_the_casey"
    })
    url = "https://scheduleit.duckdns.org/api/user/create"
    r = s.post(url, data=data)
    ret = r.json()["cookie"]
    length = len((str)(ret)) > 0
    hasCookie = False
    if length > 0:
      hasCookie = True
    self.assertTrue(hasCookie)

  def test4(self):
    #Tyler
    data = json.dumps({
      "email" : "tyty@gmail.com",
      "pass" : "sherlocked",
      "name" : "Tyler Rogers III",
      "phone" : "583-112-9082",
      "username" : "Tylerrrrr"
    })
    url = "https://scheduleit.duckdns.org/api/user/create"
    r = s.post(url, data=data)
    ret = r.json()["cookie"]
    length = len((str)(ret)) > 0
    hasCookie = False
    if length > 0:
      hasCookie = True
    self.assertTrue(hasCookie)

  #Dee
  def test5(self):
    data = json.dumps({
      "email" : "dee@hotmail.com",
      "pass" : "sherlocked",
      "name" : "Dee Reynolds",
      "phone" : "146-578-1234",
      "username" : "Dee"
    })
    url = "https://scheduleit.duckdns.org/api/user/create"
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
      "email" : "pattycakes@hotmail.com",
      "pass" : "sherlocked",
      "name" : "Patricia Jones",
      "phone" : "575-578-0001",
      "username" : "pattycakes"
    })
    url = "https://scheduleit.duckdns.org/api/user/create"
    r = s.post(url, data=data)
    ret = r.json()["cookie"]
    length = len((str)(ret)) > 0
    hasCookie = False
    if length > 0:
      hasCookie = True
    self.assertTrue(hasCookie)

  def test7(self):
    #Invalid user, already taken
    data = json.dumps({
      "email" : "invalid@hotmail.com",
      "pass" : "inv",
      "name" : "Patricia Jones",
      "phone" : "575-578-0001",
      "username" : "pattycakes"
    })
    url = "https://scheduleit.duckdns.org/api/user/create"
    r = s.post(url, data=data)
    ret = r.status_code
    self.assertEquals(500, ret)

  def test8(self):
    #Invalid arguments
    data = json.dumps(
      {"email" : "invalid@hotmail.com",
      "pass" : "inv",
      "name" : "Patricia Jones",
      "username" : "pattycakes"
    })
    url = "https://scheduleit.duckdns.org/api/user/create"
    r = s.post(url, data=data)
    ret = r.status_code
    self.assertEquals(400, ret)

  def test9(self):
    #Invalid url
    data = json.dumps({
      "email" : "invalid@hotmail.com",
      "pass" : "inv",
      "name" : "Patricia Jones",
      "phone" : "575-578-0001",
      "username" : "newPat"
    })
    url = "https://scheduleit.duckdns.org/api/user/creat"
    r = s.post(url, data=data)
    ret = r.status_code
    self.assertEquals(400, ret)
               
if __name__ == '__main__':
    unittest.main()

