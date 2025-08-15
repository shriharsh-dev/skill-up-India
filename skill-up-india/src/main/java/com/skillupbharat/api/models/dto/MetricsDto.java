package com.skillupbharat.api.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricsDto {
    private int xpDelta;
    private int streak;
    private int wpm;
    private String levelEstimate;
}

