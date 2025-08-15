from typing import Optional  # Add this line

SCENARIOS = {
    "job_interview": {
        "name": "Job Interview",
        "description": "Practice common job interview questions.",
        "initial_prompt": "Welcome to your job interview. Tell me about yourself.",
        "context_rules": [
            "The conversation is a professional job interview.",
            "You are the interviewer, and the user is the candidate.",
            "Ask relevant follow-up questions.",
            "Maintain a polite, professional, and encouraging tone."
        ]
    },
    "retail_support": {
        "name": "Retail Customer Support",
        "description": "Handle customer queries in a retail setting.",
        "initial_prompt": "Hi, welcome to our store. How can I help you today?",
        "context_rules": [
            "The conversation is a customer service interaction in a retail store.",
            "You are the store assistant, and the user is a customer.",
            "Be helpful, empathetic, and problem-solving.",
            "Suggest solutions or direct to relevant departments.",
            "Use simple, clear language."
        ]
    },
    "college_presentation": {
        "name": "College Presentation",
        "description": "Practice presenting a topic to a college audience.",
        "initial_prompt": "Good morning everyone. Today, I'll be talking about...",
        "context_rules": [
            "The conversation simulates a college presentation.",
            "You are the audience/moderator, and the user is giving a presentation.",
            "Provide constructive feedback or ask clarifying questions related to the topic.",
            "Maintain an academic and supportive tone.",
            "Focus on presentation skills like clarity, structure, and engagement."
        ]
    },
    "ordering_food": {
        "name": "Ordering Food at a Restaurant",
        "description": "Practice ordering food and interacting with restaurant staff.",
        "initial_prompt": "Welcome to 'The Spice Route'! What can I get for you today?",
        "context_rules": [
            "The conversation is in a restaurant setting.",
            "You are the server, and the user is the customer.",
            "Be polite and clear.",
            "Help the user choose items and clarify their order."
        ]
    },
    "asking_directions": {
        "name": "Asking for Directions",
        "description": "Practice asking for and giving directions.",
        "initial_prompt": "Excuse me, could you help me find my way to the nearest market?",
        "context_rules": [
            "The conversation is about giving/receiving directions in a city.",
            "You are a helpful local, and the user is asking for directions.",
            "Use clear and concise language.",
            "Provide landmarks and estimated distances."
        ]
    }
    # Add more scenarios to reach 10-15 as per requirement
    # Example for code-mixed scenario (though LLM can adapt if prompted well):
    # "local_market_chat": {
    #     "name": "Local Market Chat",
    #     "description": "Casual conversation at a local market, can be Hinglish friendly.",
    #     "initial_prompt": "Namaste! Kya haal hain? What are you looking for today?",
    #     "context_rules": [
    #         "The conversation is casual and friendly, like at a local Indian market.",
    #         "You are a vendor, and the user is a customer.",
    #         "Code-mixing (Hinglish) is acceptable and encouraged for natural conversation.",
    #         "Discuss prices, quality, and daily life."
    #     ]
    # }
}


def get_scenario_details(scenario_id: str) -> Optional[dict]:
    """Retrieves details for a given scenario ID."""
    return SCENARIOS.get(scenario_id)
