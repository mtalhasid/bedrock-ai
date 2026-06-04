package com.springinfra.service.chat;

import com.springinfra.dto.external.GeminiRequest;
import com.springinfra.dto.external.GeminiResponse;
import com.springinfra.dto.response.LlmResult;
import com.springinfra.entity.ChatMessage;
import com.springinfra.service.chat.LlmService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LlmServiceImpl implements LlmService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Override
    @CircuitBreaker(name = "ai", fallbackMethod = "fallback")
    public LlmResult generateResponse(List<ChatMessage> history, String prompt) {
        boolean isFirst = history.isEmpty();
        
        String finalPrompt = isFirst 
            ? prompt + "\n\nRespond in JSON: {\"title\": \"5 word title\", \"answer\": \"your answer\"}"
            : prompt;

        List<GeminiRequest.Content> contents = new ArrayList<>();
        
        // Add history
        for (ChatMessage msg : history) {
            contents.add(new GeminiRequest.Content(
                msg.getRole().name().toLowerCase(),
                List.of(new GeminiRequest.Part(msg.getContent()))
            ));
        }
        
        // Add current prompt
        contents.add(new GeminiRequest.Content(
            "user",
            List.of(new GeminiRequest.Part(finalPrompt))
        ));

        GeminiRequest request = new GeminiRequest(contents);

        GeminiResponse response = webClient.post()
                .uri(apiUrl + "?key=" + apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .block();

        if (response == null || response.candidates().isEmpty()) {
            throw new RuntimeException("Empty response from Gemini API");
        }

        String text = response.candidates().get(0).content().parts().get(0).text().trim();

        if (isFirst) {
            try {
                String cleanedText = text.replaceAll("```json|```", "").trim();
                return objectMapper.readValue(cleanedText, LlmResult.class);
            } catch (Exception e) {
                log.error("Failed to parse JSON response from Gemini: {}", text, e);
                return new LlmResult("Chat Session", text);
            }
        }

        return new LlmResult(null, text);
    }

    public LlmResult fallback(List<ChatMessage> history, String prompt, Throwable t) {
        log.error("Circuit Breaker triggered for prompt: {}. Error: {}", prompt, t.getMessage());
        return new LlmResult("Service Unavailable", "Gemini Service is currently unavailable. Please try again later.");
    }
}
