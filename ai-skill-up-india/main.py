import time  # For simulating speaking time for progress tracking
from typing import Dict
import os
import google.generativeai as genai
from dotenv import load_dotenv

from fastapi import FastAPI, HTTPException

from feedback_utils import construct_llm_prompt, parse_llm_response
from models import AiTurnRequest, AiTurnResponse, Feedback, GamificationUpdate, ProgressUpdate, Message
from scenarios import get_scenario_details

# Load environment variables from .env file
load_dotenv()

# Configure the Gemini API
try:
    genai.configure(api_key=os.getenv("GEMINI_API_KEY"))
except Exception as e:
    print(f"Failed to configure Gemini API: {e}")
    # Handle missing API key gracefully, maybe exit or use a mock service
    # For now, we'll print and let it fail on the first request.

app = FastAPI(
    title="Skill-Up for Bharat AI Service",
    description="Backend for AI/LLM tasks for the Skill-Up app.",
    version="1.0.0"
)

# In-memory store for user progress and gamification (for demo purposes).
user_data_store: Dict[str, Dict] = {}


@app.post("/process_turn", response_model=AiTurnResponse)
async def process_turn(request: AiTurnRequest):
    session_id = request.sessionId
    user_speech_text = request.userSpeechText
    scenario_id = request.scenarioId
    conversation_history = request.conversationHistory

    scenario_details = get_scenario_details(scenario_id)
    if not scenario_details:
        raise HTTPException(status_code=404, detail=f"Scenario '{scenario_id}' not found.")

    # --- LLM Interaction ---
    prompt = construct_llm_prompt(scenario_details, conversation_history, user_speech_text)
    # print("prompt: ", prompt) # Commented out for cleaner logs
    ai_reply_text = "I'm sorry, I couldn't generate a reply. Please try again."
    feedback = Feedback(
        clarityScore=1.0, grammarScore=1.0, vocabularyScore=1.0, paceScore=1.0,
        actionableTip="There was an issue processing your request. Please try again.",
        correctedSentence=user_speech_text
    )

    try:
        print("Hitting Gemini API for feedback")
        # Configure the model - gemini-1.5-flash is a good choice for this use case
        model = genai.GenerativeModel('gemini-1.5-flash')
        
        # Set generation config to enforce JSON output
        generation_config = genai.GenerationConfig(
            response_mime_type="application/json",
            temperature=0.2,
            top_p=0.9,
            top_k=40
        )

        # Call the Gemini API
        response = model.generate_content(prompt, generation_config=generation_config)
        llm_output_text = response.text
        
        parsed_data = parse_llm_response(llm_output_text)
        # print("parsed json response: ", parsed_data) # Commented out for cleaner logs
        
        ai_reply_text = parsed_data.get("ai_reply", ai_reply_text)
        feedback_raw = parsed_data.get("feedback", {})

        feedback = Feedback(
            clarityScore=max(1.0, min(5.0, feedback_raw.get("clarityScore", 3.0))),
            grammarScore=max(1.0, min(5.0, feedback_raw.get("grammarScore", 3.0))),
            vocabularyScore=max(1.0, min(5.0, feedback_raw.get("vocabularyScore", 3.0))),
            paceScore=max(1.0, min(5.0, feedback_raw.get("paceScore", 3.0))),
            actionableTip=feedback_raw.get("actionableTip", "Keep practicing!"),
            correctedSentence=feedback_raw.get("correctedSentence", user_speech_text)
        )

    except Exception as e:
        print(f"Error during Gemini API call or response parsing: {e}")
        # Fallback in case of API communication or parsing failure
        pass

    # --- Update Conversation History ---
    updated_history = list(conversation_history)
    updated_history.append(Message(role="assistant", content=ai_reply_text))

    # --- Gamification and Progress Tracking ---
    if session_id not in user_data_store:
        user_data_store[session_id] = {
            "xp": 0, "badges": [], "streak": 0, "session_count": 0,
            "total_speaking_time": 0, "words_spoken": 0, "turns_completed": 0,
            "last_active_timestamp": time.time()
        }
    user_data = user_data_store[session_id]

    avg_feedback_score = (
        feedback.clarityScore + feedback.grammarScore + feedback.vocabularyScore + feedback.paceScore
    ) / 4
    xp_gained = 10 + int(avg_feedback_score * 10)
    user_data["xp"] += xp_gained

    new_badges = []
    if "First Conversation" not in user_data["badges"] and user_data["turns_completed"] == 0:
        new_badges.append("First Conversation")
        user_data["badges"].append("First Conversation")

    speaking_time_this_turn_seconds = len(user_speech_text.split()) * 0.5
    user_data["total_speaking_time"] += speaking_time_this_turn_seconds
    if "10 Min Speaking" not in user_data["badges"] and user_data["total_speaking_time"] >= 600:
        new_badges.append("10 Min Speaking")
        user_data["badges"].append("10 Min Speaking")

    if "Consistency" not in user_data["badges"] and user_data["turns_completed"] >= 4:
        new_badges.append("Consistency")
        user_data["badges"].append("Consistency")

    user_data["streak"] += 1

    gamification = GamificationUpdate(
        xpGained=xp_gained,
        newBadges=new_badges,
        currentStreak=user_data["streak"]
    )

    user_data["turns_completed"] += 1
    user_data["words_spoken"] += len(user_speech_text.split())
    if user_data["turns_completed"] == 1:
        user_data["session_count"] += 1

    average_words_per_minute = 0.0
    if user_data["total_speaking_time"] > 0:
        average_words_per_minute = (user_data["words_spoken"] / user_data["total_speaking_time"]) * 60

    estimated_level = "A1"
    if user_data["xp"] > 500:
        estimated_level = "B1"
    elif user_data["xp"] > 200:
        estimated_level = "A2"

    progress = ProgressUpdate(
        sessionCount=user_data["session_count"],
        totalSpeakingTimeSeconds=int(user_data["total_speaking_time"]),
        averageWordsPerMinute=round(average_words_per_minute, 2),
        estimatedLevel=estimated_level
    )

    return AiTurnResponse(
        aiReplyText=ai_reply_text,
        feedback=feedback,
        gamification=gamification,
        progress=progress,
        updatedConversationHistory=updated_history
    )