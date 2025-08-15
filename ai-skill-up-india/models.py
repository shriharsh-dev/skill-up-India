from typing import List, Optional

from pydantic import BaseModel, Field


class Message(BaseModel):
    role: str  # "user" or "assistant"
    content: str


class AiTurnRequest(BaseModel):
    sessionId: str
    scenarioId: str
    userSpeechText: str
    conversationHistory: Optional[List[Message]] = []


class Feedback(BaseModel):
    clarityScore: float = Field(..., ge=1.0, le=5.0)  # Ensure scores are between 1.0 and 5.0
    grammarScore: float = Field(..., ge=1.0, le=5.0)
    vocabularyScore: float = Field(..., ge=1.0, le=5.0)
    paceScore: float = Field(..., ge=1.0, le=5.0)
    actionableTip: str
    correctedSentence: str


class GamificationUpdate(BaseModel):
    xpGained: int
    newBadges: List[str]
    currentStreak: int


class ProgressUpdate(BaseModel):
    sessionCount: int
    totalSpeakingTimeSeconds: int
    averageWordsPerMinute: float
    estimatedLevel: str  # A1, A2, B1


class AiTurnResponse(BaseModel):
    aiReplyText: str
    feedback: Feedback
    gamification: GamificationUpdate
    progress: ProgressUpdate
    updatedConversationHistory: List[Message]
