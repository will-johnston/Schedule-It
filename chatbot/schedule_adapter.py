# -*- coding: utf-8 -*-

from chatterbot.logic import LogicAdapter

<<<<<<< HEAD
=======

>>>>>>> 11c1d5a3978e74092f915467b172cccea71b69de
class my_schedule_adapter(LogicAdapter):
    def __init__(self, **kwargs):
        super(my_schedule_adapter, self).__init__(**kwargs)

    def can_process(self, statement):
        #returns true if statement contains all words in either of the 4 arrays
        from chatterbot.conversation import Statement
<<<<<<< HEAD
        words1 = set(['schedule', 'for'])
        words2 = set(['preferences', 'are'])
        words3 = set(['put', 'down'])
        words4 = set(['go', 'with'])
        words5 = set(['down', 'for'])
        words6 = set(['time', 'at'])
        words7 = set(['time', 'for'])
        words8 = set(['preference', 'is'])
        words9 = set(['schedule', 'at'])
        words10 = set(['down', 'at'])
        words11 = set(['go', 'for'])
        words12 = set(['want', 'at'])
        words13 = set(['times', 'for'])
        words14 = set(['times', 'at'])
        words15 = set(['preffered', 'for'])
        words16 = set(['set', 'for'])
        words17 = set(['set', 'at'])
        words18 = set(['choices', 'are'])
        words19 = set(['choice', 'is'])
        #use allwords to aggregate above arrays
        allwords = [words1, words2, words3, words4, words5, words6, words7,
                    words8, words9, words10, words11, words12, words13, words14,
                    words15, words16, words17, words18, words19]
        stmt = set(statement.text.split())
        #see if user input contains any of the combinations listed in arrays above
        for list in allwords:
            if (list.issubset(stmt)):
                return True  
        return False
=======
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

>>>>>>> 11c1d5a3978e74092f915467b172cccea71b69de


    def process(self, statement):
        from chatterbot.conversation import Statement
        import requests
<<<<<<< HEAD
        import datefinder
        import json
    
        # Let's base the confidence value on if the input provided dates
        confidence = 0
        response = ""
        #see if input provides group event and make sure event is open to user input.
        #>>>API CALL TO GET EVENT INFORMATION
        #>>>SEE IF AN EVENT NAME IS FOUND IN STATEMENT
        #see if input statement has dates
        matches = list(datefinder.find_dates(str(statement)))
        if (len(matches) != 0):
            #Create output statement
            response += "Ok. I have recorded your preferences for the time(s):\n"
            for match in matches:
                response += (str(match) + '\n')
            #call API to add information to group event
            #payload = {'preferences': ['2017-10-07 00:00:00', '2017-09-05 00:00:00'] }
            #r = requests.post(scheduleit.duckdns.org/api/group/edit_group_event, data=json.dumps(payload))
            confidence = 1
 
        response_statement = Statement(response)
        return confidence, response_statement

=======

        # figure out where to store requests for a group event
            #if database, create junction for event
            #if file system, need a different file for every event
        #response = requests.get('https://api.temperature.com/current?units=celsius')
        #data = response.json()

        # Let's base the confidence value on if the request was successful
        confidence = 1
    

        response_statement = Statement('The current temperature is 3')

        return confidence, response_statement
>>>>>>> 11c1d5a3978e74092f915467b172cccea71b69de
