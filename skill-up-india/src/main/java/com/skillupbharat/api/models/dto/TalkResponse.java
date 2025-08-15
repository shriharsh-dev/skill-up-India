package com.skillupbharat.api.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TalkResponse {
    private String sessionId;
    private int turn;
    private String transcript;
    private String reply;
    private String correctedSentence;
    private FeedbackDto feedback;
    private MetricsDto metrics;
    private Double audioDurationSec; // nullable
}
