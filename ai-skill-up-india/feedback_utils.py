import json
from typing import List, Dict

from models import Message  # Add this import to bring in the Message class definition


def construct_llm_prompt(
        scenario_details: dict,
        conversation_history: List[Message],  # Type hint should be List[Message] now
        user_speech_text: str
) -> str:
    """
    Constructs a detailed prompt for the LLM to generate a reply and feedback.
    """
    rules = "\n".join(scenario_details.get("context_rules", []))
    scenario_name = scenario_details.get("name", "General Conversation")

    # Prepare conversation history for LLM
    history_str = ""
    for msg in conversation_history:
        history_str += f"{msg.role}: {msg.content}\n"  # Change msg['role'] to msg.role and msg['content'] to msg.content
    # Ensure the last entry is the user's current speech, then prime for assistant's reply
    history_str += f"user: {user_speech_text}\nassistant: "

    prompt = f"""
    You are an AI communication coach for job seekers in India, focusing on spoken English and soft skills.
    Your goal is to provide constructive feedback and simulate real-world conversations.
    The current scenario is: "{scenario_name}".

    Scenario Rules for your reply:
    {rules}

    The conversation so far:
    {history_str}

    User's last spoken input (ASR converted text): "{user_speech_text}"
x
    Your task:
    1.  **Generate a natural, contextually relevant, and culturally sensitive reply** that continues the conversation.
        This reply should sound like a human interlocutor in the given scenario.
        It can be code-mixed (Hinglish) if appropriate for the context, but primarily focus on English.
    2.  **Analyze the user's last input** ("{user_speech_text}") and provide specific feedback based on the following metrics.
        Infer these metrics from the text, focusing on common challenges for Indian learners (e.g., direct translation, grammar issues, limited vocabulary, pronunciation clarity *inferred from word choice/structure*).

    Provide your response in a JSON format with two top-level keys: `ai_reply` and `feedback`.

    JSON Structure:
    ```json
    {{
        "ai_reply": "Your conversational reply here.",
        "feedback": {{
            "clarityScore": [float, 1.0-5.0, Higher is better. How easy was it to understand the user's intent and meaning from the text?],
            "grammarScore": [float, 1.0-5.0, How grammatically correct was the user's sentence? Focus on accuracy in tenses, subject-verb agreement, articles, and prepositions.],
            "vocabularyScore": [float, 1.0-5.0, How rich, varied, and appropriate was the vocabulary used by the user in a professional context? Did they use common Indianisms or more standard, globally understood English terms?],
            "paceScore": [float, 1.0-5.0, Based on sentence length, complexity, and overall structural fluidity, how natural does the 'pace' or flow of the sentence seem? (Note: This score is inferred purely from textual analysis, focusing on coherence and natural phrasing.)],
            "actionableTip": "One concise, highly actionable tip for immediate improvement. If the user's sentence is grammatically correct and clear (e.g., high grammarScore and clarityScore), this tip should suggest alternative ways to phrase the sentence for variety or enhanced impact (e.g., 'Your sentence is excellent! For variety, you could also say...', or 'Consider these alternative phrasings: ...'). Otherwise, the tip should focus on addressing the identified areas for improvement (e.g., 'Try using more varied sentence structures.', 'Focus on subject-verb agreement.', 'Expand your vocabulary related to X.').",
            "correctedSentence": "This field MUST always contain a version of the user's input, prefixed with a descriptive phrase. If any errors (minor or major) are detected, the output should be: 'This is the corrected sentence: [grammatically corrected and improved version of the user's input]'. For minor errors, provide a precise correction. For significant or multiple errors, rephrase the sentence for maximum clarity, naturalness, and adherence to standard English, while preserving the user's original intent. If the original user input is flawless and requires no correction, the output should be: 'Your sentence is correct: [original user_speech_text]'"
        }}
    }}
    ```
    Ensure the `ai_reply` is a natural continuation, and `feedback` is a direct analysis of the user's *last* input.
    Make sure the scores are float values between 1.0 and 5.0.
    """
    return prompt


def parse_llm_response(response_text: str) -> Dict:
    """
    Parses the JSON response from the LLM, handling potential markdown formatting.
    """
    try:
        # LLMs often wrap JSON in markdown code blocks
        if '```json' in response_text:
            json_str = response_text.split('```json')[1].split('```')[0].strip()
        else:
            json_str = response_text.strip()
        data = json.loads(json_str)
        return data
    except json.JSONDecodeError as e:
        print(f"Error decoding JSON from LLM: {e}")
        print(f"Raw LLM response (first 500 chars): {response_text[:500]}...")
        # Fallback to a default or error response if JSON parsing fails
        return {
            "ai_reply": "I'm sorry, I couldn't process that response. Could you please try again?",
            "feedback": {
                "clarityScore": 1.0, "grammarScore": 1.0, "vocabularyScore": 1.0, "paceScore": 1.0,
                "actionableTip": "Please speak clearly and try to rephrase.",
                "correctedSentence": ""
            }
        }
    except Exception as e:
        print(f"An unexpected error occurred while parsing LLM response: {e}")
        return {
            "ai_reply": "An internal error occurred. Please try again later.",
            "feedback": {
                "clarityScore": 1.0, "grammarScore": 1.0, "vocabularyScore": 1.0, "paceScore": 1.0,
                "actionableTip": "System error, please try again.",
                "correctedSentence": ""
            }
        }
