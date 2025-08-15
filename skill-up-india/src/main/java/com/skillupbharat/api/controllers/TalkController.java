//package com.skillupbharat.api.controllers;
//
//import com.skillupbharat.api.models.dto.TalkResponse;
//import com.skillupbharat.api.models.dto.TalkTextRequest;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestPart;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//
//@RestController
//@RequiredArgsConstructor
//public class TalkController {
//
//    private final CoachService coach;
//
//    @PostMapping("/talk-text")
//    public TalkResponse talkText(@Valid @RequestBody TalkTextRequest req) {
//        return coach.handleText(req.getSessionId(), req.getUserText());
//    }
//
//    @PostMapping(path = "/talk-audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public TalkResponse talkAudio(@RequestPart("file") MultipartFile file,
//                                  @RequestPart("session_id") String sessionId,
//                                  @RequestPart(value = "language_hint", required = false) String languageHint) throws IOException {
//        return coach.handleAudio(sessionId, file.getBytes(), file.getOriginalFilename(), languageHint);
//    }
//}