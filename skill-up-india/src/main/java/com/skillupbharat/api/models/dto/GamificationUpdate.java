package com.skillupbharat.api.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GamificationUpdate {
    private int xpGained;
    private List<String> newBadges; // e.g., ["First Conversation"]
    private int currentStreak;
}
