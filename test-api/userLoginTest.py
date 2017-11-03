import requests
import json
import unittest

#TEST ENDPOINTS FOR DELETE USER

class TestCreate(unittest.TestCase):
  global s
  s = requests.Session()

  def test1(self):
    #Samantha
    data = json.dumps({  
      "pass" : "nunchucks7", 
      "name" : "Samantha89" 
    })
    url = "https://scheduleit.duckdns.org/api/user/login"
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
      "pass" : "gophertoker",
      "name" : "charles_barkley"
    })
    url = "https://scheduleit.duckdns.org/api/user/login"
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
      "pass" : "absolutelynot",
      "name" : "Cut_to_the_casey"
    })
    url = "https://scheduleit.duckdns.org/api/user/login"
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
      "pass" : "sherlocked",
      "name" : "Tylerrrrr"
    })
    url = "https://scheduleit.duckdns.org/api/user/login"
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

