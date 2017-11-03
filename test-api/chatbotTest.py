import requests
import json
import unittest

#TEST ENDPOINTS FOR FRIENDS

class TestCreate(unittest.TestCase):
  global s
  s = requests.Session()

  #Dee
  def test1(self):
    #test adding an event
    data = json.dumps({
      'text': '<104> <96> event name'
    })
    url = "http://willjohnston.pythonanywhere.com/api/chatterbot/"
    r = s.post(url, data=data)
    response = r.json()["text"]
    self.assertEquals("You can now add your preferences for the event: 'event name'.", response)

  def test2(self):
    #joke
    data = json.dumps({
      'text': 'Tell me a joke'
    })
    url = "http://willjohnston.pythonanywhere.com/api/chatterbot/"
    r = s.post(url, data=data)
    response = r.json()["text"]
    self.assertEquals("Did you hear the one about the mountain goats in the andes? it was ba a a a a a d.", response)

  def test3(self):
    #joke
    data = json.dumps({
      'text': 'Hello'
    })
    url = "http://willjohnston.pythonanywhere.com/api/chatterbot/"
    r = s.post(url, data=data)
    response = r.json()["text"]
    self.assertEquals("Hi", response)

  def test4(self):
    #test scheduling
    data = json.dumps({
      'text': '<104> <96> schedule for january 3rd at 9 am'
    })
    url = "http://willjohnston.pythonanywhere.com/api/chatterbot/"
    r = s.post(url, data=data)
    response = r.json()["text"]
    self.assertEquals("Ok. I have recorded your preferences for the time(s):\n2017-01-03 09:00:00  ", response)

  def test5(self):
    #test for gibberish
    data = json.dumps({
      'text': 'sdfasdg'
    })
    url = "http://willjohnston.pythonanywhere.com/api/chatterbot/"
    r = s.post(url, data=data)
    response = r.json()["text"]
    self.assertEquals("I'm sorry, but I don't understand.", response)

  def test6(self):
    #test for gibberish
    data = json.dumps({
      'text': 'blahblahblah'
    })
    url = "http://willjohnston.pythonanywhere.com/api/chatterbot/"
    r = s.post(url, data=data)
    response = r.json()["text"]
    self.assertEquals("I'm sorry, but I don't understand.", response)

  def test7(self):
    #test for invalid schedule
    data = json.dumps({
      'text': '<104> <96> schedule for'
    })
    url = "http://willjohnston.pythonanywhere.com/api/chatterbot/"
    r = s.post(url, data=data)
    response = r.json()["text"]
    self.assertEquals("Sorry, but I don't understand. If you were trying to add your time preferences, please add a date and time to your request.", response)

def test8(self):
    #test for help call
    data = json.dumps({
      'text': 'help'
    })
    url = "http://willjohnston.pythonanywhere.com/api/chatterbot/"
    r = s.post(url, data=data)
    response = r.json()["text"]
    self.assertEquals("Feel free do ask me what I can do. Additionally, here's an example of what to say...'Movies in the park'. Then, 'Schedule me for September 3rd at 10 am'. Your date and time preference will be updated if the event takes user inputs.", response)

def test9(self):
    #test levenstein distance
    data = json.dumps({
      'text': 'evnt name'
    })
    url = "http://willjohnston.pythonanywhere.com/api/chatterbot/"
    r = s.post(url, data=data)
    response = r.json()["text"]
    self.assertEquals("You can now add your preferences for the event: 'event name'.", response)

def test9(self):
    #test alternate schedule call
    data = json.dumps({
      'text': '<104> <96> put me down for 4/4/2017 at 23:00'
    })
    url = "http://willjohnston.pythonanywhere.com/api/chatterbot/"
    r = s.post(url, data=data)
    response = r.json()["text"]
    self.assertEquals("Ok. I have recorded your preferences for the time(s): 2017-04-04 23:00:00", response)



if __name__ == '__main__':
  unittest.main()
