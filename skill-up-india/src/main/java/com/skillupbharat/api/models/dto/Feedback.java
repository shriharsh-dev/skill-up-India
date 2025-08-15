package com.skillupbharat.api.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {
    private double clarityScore; // e.g., 1.0-5.0
    private double grammarScore;
    private double vocabularyScore;
    private double paceScore;
    private String actionableTip;
    private String correctedSentence;
}
