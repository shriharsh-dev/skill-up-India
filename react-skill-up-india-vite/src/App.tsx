import React, { useEffect, useRef, useState } from 'react';
import { Container, Row, Col, Card, Button, Form, Spinner, Alert, InputGroup, Badge } from 'react-bootstrap';
import { fetchScenarios, startSession, submitTurn } from './api.ts';
import type { Message, SubmitTurnResponse } from './api.ts';

// Inline types for Web Speech API
type SpeechRecognitionResult = {
  readonly isFinal: boolean;
  readonly length: number;
  item(index: number): SpeechRecognitionAlternative;
  [index: number]: SpeechRecognitionAlternative;
};
type SpeechRecognitionAlternative = {
  readonly transcript: string;
  readonly confidence: number;
};
type SpeechRecognitionEvent = {
  readonly resultIndex: number;
  readonly results: SpeechRecognitionResultList;
};
type SpeechRecognitionResultList = {
  readonly length: number;
  item(index: number): SpeechRecognitionResult;
  [index: number]: SpeechRecognitionResult;
};
type ISpeechRecognition = {
  start: () => void;
  stop: () => void;
  abort: () => void;
  lang: string;
  continuous: boolean;
  interimResults: boolean;
  onaudioend?: () => void;
  onaudiostart?: () => void;
  onend?: () => void;
  onerror?: (event: unknown) => void;
  onnomatch?: (event: unknown) => void;
  onresult?: (event: SpeechRecognitionEvent) => void;
  onsoundend?: () => void;
  onsoundstart?: () => void;
  onspeechend?: () => void;
  onspeechstart?: () => void;
  onstart?: () => void;
};

function getSpeechRecognition(): ISpeechRecognition | null {
  const SpeechRecognitionImpl =
    (window as unknown as { SpeechRecognition?: new () => ISpeechRecognition; webkitSpeechRecognition?: new () => ISpeechRecognition }).SpeechRecognition ||
    (window as unknown as { webkitSpeechRecognition?: new () => ISpeechRecognition }).webkitSpeechRecognition;
  if (!SpeechRecognitionImpl) return null;
  return new SpeechRecognitionImpl();
}

function App() {
  const recognitionRef = useRef<ISpeechRecognition | null>(null);
  const [isListening, setIsListening] = useState(false);
  const [scenarios, setScenarios] = useState<string[]>([]);
  const [selectedScenario, setSelectedScenario] = useState('');
  const [sessionId, setSessionId] = useState('');
  const [userInput, setUserInput] = useState('');
  const [conversation, setConversation] = useState<Message[]>([]);
  const [lastTurnResult, setLastTurnResult] = useState<SubmitTurnResponse | null>(null); const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchScenarios()
      .then((scenarios: string[]) => setScenarios(scenarios))
      .catch(() => setError('Failed to load scenarios'));
    // Setup speech recognition
    const recognition = getSpeechRecognition();
    if (recognition) {
      recognition.continuous = false;
      recognition.interimResults = false;
      recognition.lang = 'en-US';
      recognition.onresult = (event: SpeechRecognitionEvent) => {
        const transcript = event.results[0][0].transcript;
        setUserInput(transcript);
        setIsListening(false);
      };
      recognition.onerror = () => setIsListening(false);
      recognition.onend = () => setIsListening(false);
      recognitionRef.current = recognition;
    }
  }, []);

  const handleMicClick = () => {
    if (!recognitionRef.current) return;
    if (isListening) {
      recognitionRef.current.stop();
      setIsListening(false);
    } else {
      recognitionRef.current.start();
      setIsListening(true);
    }
  };

  const handleStartSession = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await startSession(selectedScenario);
      setSessionId(res.sessionId);
      setConversation([{ role: 'assistant', content: res.initialPrompt }]);
      setLastTurnResult(null); // Clear previous turn's result when starting a new session
    } catch {
      setError('Failed to start session');
    }
    setLoading(false);
  };

  // Speak the corrected sentence when feedback changes
  useEffect(() => {
    if (lastTurnResult && lastTurnResult.feedback && lastTurnResult.feedback.correctedSentence) {
      const utterance = new window.SpeechSynthesisUtterance(lastTurnResult.feedback.correctedSentence);
      window.speechSynthesis.speak(utterance);
    }
  }, [lastTurnResult]);

  const handleSubmitTurn = async () => {
    if (!userInput.trim()) return;
    setLoading(true);
    setError('');
    try {
      setConversation((prev: Message[]) => [...prev, { role: 'user', content: userInput }]);
      const res = await submitTurn(sessionId, userInput); // submitTurn now returns SubmitTurnResponse
      setConversation((prev: Message[]) => [
        ...prev,
        { role: 'assistant', content: res.aiReplyText }, // Use aiReplyText from the response
      ]);
      setLastTurnResult(res); // Store the entire response
      setUserInput('');
    } catch { // Catch the error to display it
      console.error("Failed to submit turn:");
      setError('Failed to submit turn. ');
    }
    setLoading(false);
  };

  return (
    <Container style={{ maxWidth: 700, marginTop: 40 }}>
      <Row className="justify-content-center mb-4">
        <Col md={12} className="text-center">
          <img src="/logo.jpg" alt="App Logo" style={{ maxWidth: 180, marginBottom: 16, borderRadius: 16, boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }} />
        </Col>
      </Row>
      <Row className="justify-content-center mb-4">
        <Col md={10} lg={8}>
          <h2 className="mb-3 text-center">Skill Up India - AI Conversation</h2>
          {error && <Alert variant="danger">{error}</Alert>}
          <Card className="mb-3">
            <Card.Body>
              <Form>
                <Row className="g-2 align-items-center">
                  <Col xs={12} sm={8}>
                    <Form.Group className="w-100">
                      <Form.Label>Scenario</Form.Label>
                      <Form.Select
                        value={selectedScenario}
                        onChange={(e: React.ChangeEvent<HTMLSelectElement>) => setSelectedScenario(e.target.value)}
                        disabled={loading}
                      >
                        <option value="">Select a scenario</option>
                        {scenarios.map((s: string) => (
                          <option key={s} value={s}>{s}</option>
                        ))}
                      </Form.Select>
                    </Form.Group>
                  </Col>
                  <Col xs={12} sm={4} className="d-grid">
                    <Button
                      variant="primary"
                      onClick={handleStartSession}
                      disabled={!selectedScenario || loading}
                    >
                      {loading ? <Spinner size="sm" animation="border" /> : 'Start Session'}
                    </Button>
                  </Col>
                </Row>
              </Form>
            </Card.Body>
          </Card>
          <Card className="mb-3" style={{ minHeight: 220 }}>
            <Card.Body style={{ background: '#fafafa' }}>
              {conversation.length === 0 && <div className="text-muted text-center">No conversation yet.</div>}
              {conversation.map((msg: Message, i: number) => (
                <div
                  key={i}
                  className={`d-flex mb-2 ${msg.role === 'user' ? 'justify-content-end' : 'justify-content-start'}`}
                >
                  <div
                    style={{
                      display: 'inline-flex',
                      alignItems: 'center',
                      background: msg.role === 'user' ? '#e6f7ff' : '#f1f3f4',
                      borderRadius: 16,
                      padding: '8px 14px',
                      maxWidth: '80%',
                      boxShadow: '0 1px 2px rgba(0,0,0,0.04)',
                      fontSize: 16,
                    }}
                  >
                    <span style={{
                      fontWeight: 600,
                      color: msg.role === 'user' ? '#007bff' : '#333',
                      marginRight: 8,
                      whiteSpace: 'nowrap',
                    }}>
                      {msg.role === 'user' ? 'You:' : 'AI:'}
                    </span>
                    <span style={{ wordBreak: 'break-word' }}>{msg.content}</span>
                  </div>
                </div>
              ))}
            </Card.Body>
          </Card>
          {sessionId && (
            <Card className="mb-3">
              <Card.Body>
                <InputGroup>
                  <Form.Control
                    type="text"
                    value={userInput}
                    onChange={(e: React.ChangeEvent<HTMLInputElement>) => setUserInput(e.target.value)}
                    onKeyDown={(e: React.KeyboardEvent<HTMLInputElement>) => e.key === 'Enter' && handleSubmitTurn()}
                    placeholder="Type your message..."
                    disabled={loading}
                  />
                  <Button
                    variant={isListening ? 'danger' : 'secondary'}
                    onClick={handleMicClick}
                    disabled={loading || !('webkitSpeechRecognition' in window)}
                  >
                    {isListening ? '🎤 Stop' : '🎤 Speak'}
                  </Button>
                  <Button
                    variant="success"
                    onClick={handleSubmitTurn}
                    disabled={loading || !userInput.trim()}
                  >
                    Send
                  </Button>
                </InputGroup>
              </Card.Body>
            </Card>
          )}
          {/* Display feedback, gamification, and progress from lastTurnResult */}
          {lastTurnResult && ( // Check if lastTurnResult exists
            <Alert variant="info">
              {/* Feedback Section */}
              <b>Feedback:</b><br />
              Clarity: {lastTurnResult.feedback.clarityScore}, Grammar: {lastTurnResult.feedback.grammarScore}, Vocabulary: {lastTurnResult.feedback.vocabularyScore}, Pace: {lastTurnResult.feedback.paceScore}<br />
              Tip: {lastTurnResult.feedback.actionableTip}<br />
              Corrected: {lastTurnResult.feedback.correctedSentence}<br />

              {/* Gamification Section */}
              {lastTurnResult.gamification && (
                <>
                  <p className="mb-1 mt-2">
                    <b>XP Awarded:</b> {lastTurnResult.gamification.xpGained} ✨
                  </p>
                  {lastTurnResult.gamification.newBadges && lastTurnResult.gamification.newBadges.length > 0 && (
                    <p className="mb-0">
                      <b>Badges Earned:</b>{' '}
                      {lastTurnResult.gamification.newBadges.map((badge, index) => (
                        <Badge key={index} bg="success" className="me-1">
                          {badge}
                        </Badge>
                      ))}
                    </p>
                  )}
                </>
              )}

              {/* Progress Section */}
              {lastTurnResult.progress && (
                <div className="mt-2">
                  <b>Current Progress:</b><br />
                  Sessions: {lastTurnResult.progress.sessionCount},
                  Speaking Time: {Math.round(lastTurnResult.progress.totalSpeakingTimeSeconds / 60)} min,
                  Avg WPM: {lastTurnResult.progress.averageWordsPerMinute},
                  Level: {lastTurnResult.progress.estimatedLevel}
                </div>
              )}
            </Alert>
          )}
        </Col>
      </Row>
    </Container>
  );
}

export default App;


