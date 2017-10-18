from chatterbot.logic import LogicAdapter


class schedule_adapter(LogicAdapter):
    def __init__(self, **kwargs):
        super(schedule_adapter, self).__init__(**kwargs)

    def can_process(self, statement):
        #returns true if statement contains all words in either of the 4 arrays
        words1 = ['schedule', 'for', 'at']
        words2 = ['preferences', 'are']
        words3 = ['put', 'me', 'down']
        words4 = ['let\'s', 'go', 'with']

        if all(x in statement.text.split() for x in words1)
            return True
        elif all(x in statement.text.split() for x in words2)
            return True
        elif all(x in statement.text.split() for x in words3)
            return True
        elif all(x in statement.text.split() for x in words4)
            return True
        else:
            return False



    def process(self, statement):
        from chatterbot.conversation import Statement
        import requests

        # figure out where to store requests for a group event
            #if database, create junction for event
            #if file system, need a different file for every event
        response = requests.get('https://api.temperature.com/current?units=celsius')
        data = response.json()

        # Let's base the confidence value on if the request was successful
        if response.status_code == 200:
            confidence = 1
        else:
            confidence = 0

        temperature = data.get('temperature', 'unavailable')

        response_statement = Statement('The current temperature is {}'.format(temperature))

        return confidence, response_statement