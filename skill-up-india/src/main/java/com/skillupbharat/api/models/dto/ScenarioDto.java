package com.skillupbharat.api.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioDto {
    private String id;
    private String title;
    private String level; // A1/A2/B1
    private String language; // hi-en
    private String intro;
    private List hints;
}
