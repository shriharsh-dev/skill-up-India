import axios from 'axios';

const API_BASE = 'http://localhost:8080/api/sessions';

export async function fetchScenarios() {
  const res = await axios.get(`${API_BASE}/scenarios`);
  return res.data;
}

export async function startSession(scenarioId) {
  const res = await axios.post(`${API_BASE}/start`, null, { params: { scenarioId } });
  return res.data;
}

export async function submitTurn(sessionId, userSpeechText) {
  const res = await axios.post(`${API_BASE}/${sessionId}/submit_turn`, null, { params: { userSpeechText } });
  return res.data;
}
