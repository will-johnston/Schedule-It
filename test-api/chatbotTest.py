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

  def test10(self):
    #show events (no events)
    data = json.dumps({
      "text" : "show events",    
    })
    url = "http://willjohnston.pythonanywhere.com/api/chatterbot/"
    r = s.post(url, data = data)
    self.assertTrue(r.status_code == 200)
    response = r.json()["text"]
    self.assertEquals("Here are all the open-ended events for this group: ", response) 

  def test11(self):
    #create open-ended event
    data = json.dumps({
      "text" : "<225> <46> create event, breakfast for dinner, open-ended, December 7th at 5 pm, bring bacon",    
    })
    url = "http://willjohnston.pythonanywhere.com/api/chatterbot/"
    r = s.post(url, data = data)
    self.assertTrue(r.status_code == 200)
    response = r.json()["text"]
    self.assertEquals("Event created: breakfast for dinner", response)

  def test12(self):
    #create set event
    data = json.dumps({
      "text" : "<225> <46> create event, mandatory meeting, set, December 6th at 2 pm, bring a can-do attitude",    
    })
    url = "http://willjohnston.pythonanywhere.com/api/chatterbot/"
    r = s.post(url, data = data)
    self.assertTrue(r.status_code == 200)
    response = r.json()["text"]
    self.assertEquals("Event created: mandatory meeting", response)
  
  def test13(self):
   #show events (breakfast for dinner event)
    data = json.dumps({
      "text" : "<225> <46> show events",    
    })
    url = "http://willjohnston.pythonanywhere.com/api/chatterbot/"
    r = s.post(url, data = data)
    self.assertTrue(r.status_code == 200)
    response = r.json()["text"]
    self.assertEquals("Here are all the open-ended events for this group: breakfast for dinner", response)

  def test14(self):
    #start time inputs
    data = json.dumps({
      "text" : "<225> <46> breakfast for dinner",    
    })
    url = "http://willjohnston.pythonanywhere.com/api/chatterbot/"
    r = s.post(url, data = data)
    self.assertTrue(r.status_code == 200)
    response = r.json()["text"]
    self.assertEquals("You can now add your preferences for the event: 'breakfast for dinner'", response)

  def test15(self):
    #start time inputs (test levenshtein distance)
    data = json.dumps({
      "text" : "<225> <46> breakfsat for dinner",    
    })
    url = "http://willjohnston.pythonanywhere.com/api/chatterbot/"
    r = s.post(url, data = data)
    self.assertTrue(r.status_code == 200)
    response = r.json()["text"]
    self.assertEquals("You can now add your preferences for the event: 'breakfast for dinner'", response)

  def test16(self):
    #test scheduling
    data = json.dumps({
      'text': '<225> <46> schedule for December 5th at 6 pm'
    })
    url = "http://willjohnston.pythonanywhere.com/api/chatterbot/"
    r = s.post(url, data=data)
    response = r.json()["text"]
    self.assertEquals("Ok. I have recorded your preferences for the time(s): 2017-12-05 18:00:00", response)

  def test17(self):
    #test scheduling
    data = json.dumps({
      'text': '<225> <46> schedule for December 5th at 7 pm'
    })
    url = "http://willjohnston.pythonanywhere.com/api/chatterbot/"
    r = s.post(url, data=data)
    response = r.json()["text"]
    self.assertEquals("Ok. I have recorded your preferences for the time(s): 2017-12-05 19:00:00", response)

  def test18(self):
    #test scheduling multiple times
    data = json.dumps({
      'text': '<225> <46> schedule for December 5th at 7 pm and December 5th at 8 pm'
    })
    url = "http://willjohnston.pythonanywhere.com/api/chatterbot/"
    r = s.post(url, data=data)
    response = r.json()["text"]
    self.assertEquals("Ok. I have recorded your preferences for the time(s): 2017-12-05 19:00:00, 2017-12-05 20:00:00", response)

  def test19(self):
    #test help
    data = json.dumps({
      'text': '<225> <46> help'
    })
    url = "http://willjohnston.pythonanywhere.com/api/chatterbot/"
    r = s.post(url, data=data)
    response = r.json()["text"]
    self.assertEquals("Hi! Here are some commands you can use. Schedule events (must be open-ended) = \"event name\", \"schedule me for [date]\", Create event = \"create event, [event name], [set/open-ended], [date/expiration_time], [description]\", Show events = \"show events\"")

  def test20(self):
    #test 'what can you tell me?'
    data = json.dumps({
      'text': '<225> <46> what can you tell me?'
    })
    url = "http://willjohnston.pythonanywhere.com/api/chatterbot/"
    r = s.post(url, data=data)
    response = r.json()["text"]
    self.assertEquals("My main function is to help schedule events. However, I can also create events for you or tell you events that you can give your time preferences to. Type \"help\" for a list of commands.")

  def test21(self):
    #test 'what can you tell me?'
    data = json.dumps({
      'text': '<225> <46> Could you explain event time to me?'
    })
    url = "http://willjohnston.pythonanywhere.com/api/chatterbot/"
    r = s.post(url, data=data)
    response = r.json()["text"]
    self.assertEquals("Either an event is set or open-ended. When creating an event, you may choose either. If you set the event to open-ended, tell me your time preferences. An expiration date will determine when to process all the preferences and calculate the best time.")

def test22(self):
    #test what can you do?
    data = json.dumps({
      'text': '<225> <46> What can you do?'
    })
    url = "http://willjohnston.pythonanywhere.com/api/chatterbot/"
    r = s.post(url, data=data)
    response = r.json()["text"]
    self.assertEquals("Hi! Here are some commands you can use. Schedule events (must be open-ended) = \"event name\", \"schedule me for [date]\", Create event = \"create event, [event name], [set/open-ended], [date/expiration_time], [description]\", Show events = \"show events\"")



if __name__ == '__main__':
  unittest.main()
