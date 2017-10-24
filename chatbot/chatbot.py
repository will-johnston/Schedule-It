# -*- coding: utf-8 -*-
from chatterbot import ChatBot
from chatterbot.trainers import ChatterBotCorpusTrainer


# Create a new chat bot named Charlie
chatbot = ChatBot(
    'Clarence',
    trainer='chatterbot.trainers.ListTrainer',
    logic_adapters= [
<<<<<<< HEAD
         {   'import_path': 'schedule_adapter.my_schedule_adapter' },
=======
         #   'schedule_adapter.my_schedule_adapter',
>>>>>>> 11c1d5a3978e74092f915467b172cccea71b69de
            {
            "import_path":'chatterbot.logic.BestMatch',
            'statement_comparison_function': 'chatterbot.comparisons.levenshtein_distance'


            },
            {
             'import_path': 'chatterbot.logic.LowConfidenceAdapter',
            'threshold': 0.65,
            'default_response': 'I am sorry, but I do not understand.'
            }
        ],
    input_adapter='chatterbot.input.TerminalAdapter',
    output_adapter='chatterbot.output.TerminalAdapter'
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
    "Hello",
    "Hi, can I help you?"
])
chatbot.train([
    "Hi",
    "Hello there!",
])
chatbot.train([
    "What is your name",
    "My name is Clarence.",
])

chatbot.train([
    "What's up?",
    "The sky.",
])
chatbot.train([
   "How are you?",
   "I am just splendid."
])
chatbot.train([
   "Hey",
   "Hello, what can I do for you?"
])
chatbot.train([
   "Greetings",
   "Hey, yourself."
])
chatbot.train([
   "Howdy",
   "Hello!"
])
chatbot.train([
   "Ello there",
   "Hey!"
])
chatbot.train([
   "How was your day?",
   "Same old, same old."
])
chatbot.train([
   "I have missed you.",
   "I don't know how to feel."
])
chatbot.train([
   "Hey there",
   "Hello!"
])



#how chatbot can help
event_time_explanation =  """Event timing works in two ways.
       Either an exact time is set or the best time is found from the group members preferences.
       When creating an event, you will have the option to choose.
       \nIf you set the timing to look for the best time, I will ask all group members who wish to attend their top two preferences.
       An expiration date will determine when to process all the preferences and calculate the best time."""
what_bot_does =  """My main function is to help schedule events. However, I can also give you information on any events for the group.
       This could range from event names and descriptions to timing (whether an event time is set or flexible to your preferences)."""


chatbot.train([
    "What can you tell me?",
    what_bot_does,
<<<<<<< HEAD
    ]);
chatbot.train([
=======
>>>>>>> 11c1d5a3978e74092f915467b172cccea71b69de
    "Could you explain event time to me?",
    event_time_explanation
    ]);
chatbot.train([
    "What can you do?",
    "I can do many things!" + what_bot_does
     ]);
chatbot.train([
    "How do you work?",
    what_bot_does
    ]);
chatbot.train([
    "I don't know what to say.",
    "Here's an example: 'What time is the Movies in the Park event?'"
    ]);
chatbot.train([
    "Give me an example of what to say.",
    "Sure. You could ask, 'When does the group input for My Event Example expire?' I will tell you how much longer until I find the best time."
    ]);

chatbot.train([
    "Help",
    "Hello! Here's what I can do for you. I can give you information on any group events. This could range from event names and descriptions to timing (whether an event time is set or flexible to your preferences).",
    "Could you explain event time to me?",
    "Event timing works in two ways. Either an exact time is set or the best time is found from the group members preferences. When creating an event, you will have the option to choose. \nIf you set the timing to look for the best time, I will ask all group members who wish to attend their top two preferences. An expiration date will determine when to process all the preferences and calculate the best time."
    ]);

#chatbot main functionality
chatbot.train([
    "What is the event name?",
    event_name(),
    
    "How much longer do I have until I need to state the time preferences for the event?",
    event_preferences_expiration(),
    "I want the event to be on the 21st at 11.",
    event_time_preferences()
    ]);
chatbot.train([
    "Name of the event?",
    event_name()
    ])
chatbot.train([
    "Tell me the name of the event.",
    event_name()
    ])
chatbot.train([
    "What is the event time?",
    event_time()
    ])

chatbot.set_trainer(ChatterBotCorpusTrainer)

chatbot.train(
<<<<<<< HEAD
#    "chatterbot.corpus.english"
=======
    "chatterbot.corpus.english"
>>>>>>> 11c1d5a3978e74092f915467b172cccea71b69de
)

# Get a response to the input text 'How are you?'
print("Type something to begin...")

# The following loop will execute each time the user enters input
while True:
    try:
        # We pass None to this method because the parameter
        # is not used by the TerminalAdapter
        bot_input = chatbot.get_response(None)

    # Press ctrl-c or ctrl-d on the keyboard to exit
    except (KeyboardInterrupt, EOFError, SystemExit):
        break
