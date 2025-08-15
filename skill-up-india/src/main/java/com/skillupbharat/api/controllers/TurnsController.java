//package com.skillupbharat.api.controllers;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Collections;
//import java.util.Map;
//
//@RestController
//@RequiredArgsConstructor
//public class TurnsController {
//
//    private final SessionService sessions;
//
//    @GetMapping("/sessions/{sessionId}/turns")
//    public Map<String, Object> turns(@PathVariable String sessionId,
//                                     @RequestParam(name = "limit", defaultValue = "10") int limit) {
//        var s = sessions.get(sessionId);
//        var last = s.getHistory() != null ? s.getHistory() : Collections.emptyList();
//        int from = Math.max(0, last.size() - limit);
//        return Map.of("turns", last.subList(from, last.size()));
//    }
//}