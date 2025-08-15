package com.skillupbharat.api.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiTurnRequest {
    private String sessionId;
    private String scenarioId;
    private String userSpeechText; // Text from client-side ASR
    private List<Message> conversationHistory; // For LLM context
}
