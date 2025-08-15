package com.skillupbharat.api.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiTurnResponse {
    private String aiReplyText; // Text for client-side TTS
    private Feedback feedback;
    private GamificationUpdate gamification;
    private ProgressUpdate progress;
    private List<Message> updatedConversationHistory; // For next turn
}
