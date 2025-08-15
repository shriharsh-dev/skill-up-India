package com.skillupbharat.api.services;


import com.skillupbharat.api.models.dto.AiTurnRequest;
import com.skillupbharat.api.models.dto.AiTurnResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class AIServiceClient {

    private final RestTemplate restTemplate;
    @Value("${app.python.url}")
    private String pythonAiServiceUrl;

    public AiTurnResponse processUserTurn(AiTurnRequest request) {
        String url = pythonAiServiceUrl + "/process_turn";
        log.info("Calling Python AI service at: {}", url);
        log.info("Request: {}", request);
        try {
            var aiTurnResponse = restTemplate.postForObject(url, request, AiTurnResponse.class);
            log.info("Response from AI: {}", aiTurnResponse);
            return aiTurnResponse;
        } catch (HttpClientErrorException.UnprocessableEntity e) {
            log.error("422 Unprocessable Entity from Python AI service:");
            log.error("Response Body: " + e.getResponseBodyAsString()); // THIS WILL SHOW THE DETAIL
            throw new RuntimeException("Validation error from AI service", e);
        } catch (Exception e) {
            System.err.println("Error calling Python AI service: " + e.getMessage());
            throw new RuntimeException("Failed to get response from AI service", e);
        }
    }
}