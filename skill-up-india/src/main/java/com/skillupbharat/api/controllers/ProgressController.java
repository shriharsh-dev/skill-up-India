//package com.skillupbharat.api.controllers;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/me")
//public class ProgressController {
//
//    private final SessionService sessions;
//
//    @GetMapping("/progress")
//    public Map<String, Object> progress(@RequestHeader(value = "X-User-Id", required = false) String userId) {
//        return Map.of(
//                "days_active", 3,
//                "streak", 2,
//                "total_sessions", sessions.list().size(),
//                "total_speaking_time_sec", sessions.list().stream().mapToLong(s -> s.getTotalSpeakingTimeSec()).sum(),
//                "avg_wpm", 80,
//                "levels", Map.of("A1", 5, "A2", 1),
//                "badges", List.of("First Conversation", "10 Min Speaking")
//        );
//    }
//}