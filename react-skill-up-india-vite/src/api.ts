import axios from 'axios';

export interface Feedback {
  clarityScore: number;
  grammarScore: number;
  vocabularyScore: number;
  paceScore: number;
  actionableTip: string;
  correctedSentence: string;
}

export interface Message {
  role: string;
  content: string;
}

export interface Gamification {
  xpGained: number;
  newBadges: string[];
  currentStreak: number;
}

export interface SubmitTurnResponse {
  aiReplyText: string;
  updatedConversationHistory: Message[];
  feedback: Feedback; // Not optional
  gamification: Gamification; // Not optional
  progress: ProgressUpdate; // Added
}
// NEW: Matches main.py's ProgressUpdate
export interface ProgressUpdate {
  sessionCount: number;
  totalSpeakingTimeSeconds: number;
  averageWordsPerMinute: number;
  estimatedLevel: string;
}

const API_BASE = 'http://localhost:8080/api/sessions';

export async function fetchScenarios(): Promise<string[]> {
  const res = await axios.get(`${API_BASE}/scenarios`);
  return res.data;
}

export async function startSession(scenarioId: string): Promise<{ sessionId: string; initialPrompt: string; }> {
  const res = await axios.post(`${API_BASE}/start`, null, { params: { scenarioId } });
  return res.data;
}

export async function submitTurn(sessionId: string, userSpeechText: string): Promise<TurnResponse> {
  const res = await axios.post(`${API_BASE}/${sessionId}/submit_turn`, null, { params: { userSpeechText } });
  return res.data;
}
