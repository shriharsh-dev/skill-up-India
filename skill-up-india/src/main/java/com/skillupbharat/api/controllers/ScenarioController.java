//package com.skillupbharat.api.controllers;
//
//
//import com.skillupbharat.api.models.dto.ScenarioDto;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.Map;
//
//@RestController
//@RequiredArgsConstructor
//public class ScenarioController {
//
//    private final ScenarioService scenarios;
//
//    @GetMapping("/scenarios")
//    public Map<String, Object> list() {
//        return Map.of("scenarios", scenarios.list());
//    }
//
//    @GetMapping("/scenarios/{id}")
//    public ScenarioDto get(@PathVariable String id) {
//        return scenarios.get(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Scenario not found"));
//    }
//}