package com.skillupbharat.api.controllers;

import com.skillupbharat.api.models.dto.AiTurnRequest;
import com.skillupbharat.api.models.dto.AiTurnResponse;
import com.skillupbharat.api.models.dto.Message;
import com.skillupbharat.api.services.AIServiceClient;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/sessions")
@Slf4j
public class SessionController {

    private final AIServiceClient aiServiceClient;

    private final Map<String, SessionState> sessionStates = new ConcurrentHashMap<>();
    private final List<String> availableScenarios = List.of(
            "job_interview",
            "retail_support",
            "college_presentation",
            "ordering_food",
            "asking_directions"
    );

    public SessionController(AIServiceClient aiServiceClient) {
        this.aiServiceClient = aiServiceClient;
    }


    @PostMapping("/start")
    @Validated
    public ResponseEntity<Map<String, String>> startSession(@RequestParam @NotBlank String scenarioId) {
        if (scenarioId == null || scenarioId.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "scenarioId is required."));
        }

        String sessionId = UUID.randomUUID().toString();
        // Initialize conversation history with the AI's first prompt for the scenario
        String initialPrompt = getScenarioInitialPrompt(scenarioId);
        List<Message> initialHistory = new ArrayList<>();
        initialHistory.add(new Message("assistant", initialPrompt));

        sessionStates.put(sessionId, new SessionState(scenarioId, initialHistory));

        return ResponseEntity.ok(Map.of("sessionId", sessionId, "initialPrompt", initialPrompt));
    }

    // This method simulates fetching initial prompts for scenarios.
    // In a real application, this would come from a database or configuration.
    private String getScenarioInitialPrompt(String scenarioId) {
        return switch (scenarioId) {
            case "job_interview" -> "Welcome to your job interview. Tell me about yourself.";
            case "retail_support" -> "Hi, welcome to our store. How can I help you today?";
            case "college_presentation" -> "Good morning everyone. Today, I'll be talking about...";
            case "ordering_food" -> "Welcome to our virtual restaurant! What can I get for you today?";
            case "asking_directions" -> "I can help you find your way. Where are you trying to go?";
            default -> "Let's start a conversation!";
        };
    }


    @PostMapping("/{sessionId}/submit_turn")
    public ResponseEntity<AiTurnResponse> submitTurn(
            @PathVariable String sessionId,
            @RequestParam @NotBlank String userSpeechText) {
        SessionState sessionState = sessionStates.get(sessionId);
        if (sessionState == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }


        if (userSpeechText == null || userSpeechText.isEmpty()) {
            return ResponseEntity.badRequest().body(null); // Or a specific error DTO
        }

        List<Message> currentHistory = sessionState.getConversationHistory();

        currentHistory.add(new Message("user", userSpeechText));

        AiTurnRequest aiRequest = new AiTurnRequest(
                sessionId,
                sessionState.getScenarioId(),
                userSpeechText,
                currentHistory
        );

        AiTurnResponse aiResponse;
        try {
            aiResponse = aiServiceClient.processUserTurn(aiRequest);
        } catch (RuntimeException e) {
            log.error("Failed to process turn with AI service: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AiTurnResponse("Sorry, I'm having trouble connecting to the AI. Please try again.", null, null, null, currentHistory));
        }

        sessionState.setConversationHistory(aiResponse.getUpdatedConversationHistory());

        return ResponseEntity.ok(aiResponse);
    }

    @GetMapping("/scenarios")
    public List<String> getScenarios() {
        return availableScenarios;
    }

    // Simple in-memory session state class
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class SessionState {
        private String scenarioId;
        private List<Message> conversationHistory = new ArrayList<>();
    }
}
