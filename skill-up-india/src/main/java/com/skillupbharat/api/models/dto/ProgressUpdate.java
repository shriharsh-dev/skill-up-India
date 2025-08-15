package com.skillupbharat.api.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressUpdate {
    private int sessionCount;
    private int totalSpeakingTimeSeconds;
    private double averageWordsPerMinute;
    private String estimatedLevel; // A1, A2, B1
}

