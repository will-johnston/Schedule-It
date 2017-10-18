from chatterbot import ChatBot

# Create a new chat bot named Charlie
chatbot = ChatBot(
    'Clarence',
    trainer='chatterbot.trainers.ListTrainer',
    logic_adapters= [
        {
            'import_path': schedule_adapter.schedule_adapter
        },
        {
            'import_path': 'chatterbot.logic.BestMatch',
            'comparison_function': 'chatterbot.comparisons.levenshtein_distance',
            'response_selection_method': 'chatterbot.response_selection.get_first_response'
        },
        {
            'import_path': 'chatterbot.logic.MathematicalEvaluation',
            'threshold': '0.65'
        }
        ],
    preprocessors=[ 'chatterbot.preprocessors.clean_whitespace']
)

#database calls
def event_name():
    return "Sample event name"
def event_time():
    return "Sample event time"
def event_description():
    return "Sample event description"
def event_time_preferences():
    return "OK, I have set your time preferences."
def event_preferences_expiration():
    return "The event 'X' has Y time until group member preferences close"

#chatbot greetings
chatbot.train([
    "Hello"
    "Hi, can I help you?",
    "Sure, I'd to book a flight to Iceland.",
    test(),
])


#how chatbot can help
chatbot.train([
    "What can you tell me?",
    "I can give you information on any group events. This could range from event names and descriptions to timing (whether an event time is set or flexible to your preferences).",
    "Could you explain event time to me?",
    "Event timing works in two ways. Either an exact time is set or the best time is found from the group members preferences. When creating an event, you will have the option to choose. \nIf you set the timing to look for the best time, I will ask all group members who wish to attend their top two preferences. An expiration date will determine when to process all the preferences and calculate the best time."
    ]);

#chatbot main functionality
chatbot.train([
    "What is the event name?",
    event_name(),
    "What is the event time?",
    event_time(),
    "How much longer do I have until I need to state the time preferences for the event?",
    event_preferences_expiration(),
    "I want the event to be on the 21st at 11.",
    event_time_preferences()
    ]);

# Get a response to the input text 'How are you?'
response = chatbot.get_response('Schedule the event for the 18th at 9.')

print(response)
