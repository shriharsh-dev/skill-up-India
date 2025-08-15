//package com.skillupbharat.api.controllers;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestClient;
//
//import java.util.Map;
//
//@RestController
//@RequiredArgsConstructor
//public class HealthController {
//
//    private final RestClient pythonRestClient;
//
//    @Value("${app.python.url:}")
//    private String pythonUrl;
//
//    @GetMapping("/health")
//    public Map<String, Object> health() {
//        String py = "not_configured";
//        if (pythonUrl != null && !pythonUrl.isBlank()) {
//            try {
//                ResponseEntity<Map<String, Object>> resp = pythonRestClient.get()
//                        .uri("/health")
//                        .retrieve()
//                        .toEntity(new ParameterizedTypeReference<>() {
//                        });
//                py = resp.getStatusCode().is2xxSuccessful() ? "up" : ("down:" + resp.getStatusCode().value());
//            } catch (Exception e) {
//                py = "down";
//            }
//        }
//        return Map.of("ok", true, "python", py, "version", "1.0.0");
//    }
//}