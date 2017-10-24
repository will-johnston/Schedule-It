# -*- coding: utf-8 -*-

from chatterbot.logic import LogicAdapter

class my_schedule_adapter(LogicAdapter):
    def __init__(self, **kwargs):
        super(my_schedule_adapter, self).__init__(**kwargs)

    def can_process(self, statement):
        #returns true if statement contains all words in either of the 4 arrays
        from chatterbot.conversation import Statement
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


    def process(self, statement):
        from chatterbot.conversation import Statement
        import requests
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

