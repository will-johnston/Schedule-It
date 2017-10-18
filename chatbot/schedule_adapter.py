# -*- coding: utf-8 -*-

from chatterbot.logic import LogicAdapter


class my_schedule_adapter(LogicAdapter):
    def __init__(self, **kwargs):
        super(my_schedule_adapter, self).__init__(**kwargs)

    def can_process(self, statement):
        #returns true if statement contains all words in either of the 4 arrays
        from chatterbot.conversation import Statement
        words1 = ['schedule', 'for', 'at']
        words2 = ['preferences', 'are']
        words3 = ['put', 'me', 'down']
        words4 = ['let\'s', 'go', 'with']

        #print statement.text.split()

        
        #if words1.issubset(statement.text.split())
         #   return True
        #elif all(x in statement.text.split() for x in words2)
         #   return True
        #elif all(x in statement.text.split() for x in words3)
         #   return True
        #elif all(x in statement.text.split() for x in words4)
         #   return True
        #else:
         #   return False
        #else:
         #   return False
        return True



    def process(self, statement):
        from chatterbot.conversation import Statement
        import requests

        # figure out where to store requests for a group event
            #if database, create junction for event
            #if file system, need a different file for every event
        #response = requests.get('https://api.temperature.com/current?units=celsius')
        #data = response.json()

        # Let's base the confidence value on if the request was successful
        confidence = 1
    

        response_statement = Statement('The current temperature is 3')

        return confidence, response_statement