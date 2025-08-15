package com.skillupbharat.api.models.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TalkTextRequest {
    @NotBlank
    private String sessionId;
    @NotBlank
    private String userText;
    private String languageHint;
}
