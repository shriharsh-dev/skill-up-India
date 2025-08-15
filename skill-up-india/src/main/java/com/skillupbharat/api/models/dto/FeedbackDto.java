package com.skillupbharat.api.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDto {
    private int clarity; // 1-5
    private List<String> grammarNotes;
    private String vocabTip;
    private String pace; // slow/ok/fast
    private String tip;
}
