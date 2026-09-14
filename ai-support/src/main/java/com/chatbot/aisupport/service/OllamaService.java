package com.chatbot.aisupport.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@Service
public class OllamaService {

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    public String getAIResponse(String prompt) {

        String apiKey = System.getenv("GROQ_API_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return "⚠️ API key is missing. Please add GROQ_API_KEY to Render Environment Variables.";
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setRequestFactory(
                    new org.springframework.http.client.SimpleClientHttpRequestFactory() {{
                        setConnectTimeout(5000);
                        setReadTimeout(60000);
                    }}
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> request = new HashMap<>();
            request.put("model", "openai/gpt-oss-20b");
            
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", "You are an AI customer service assistant. Answer simply and directly in 2-3 lines.");
            messages.add(systemMsg);
            
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", prompt);
            messages.add(userMsg);

            request.put("messages", messages);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            GROQ_URL,
                            HttpMethod.POST,
                            entity,
                            new org.springframework.core.ParameterizedTypeReference<>() {}
                    );

            if (response.getBody() != null && response.getBody().containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> messageObj = (Map<String, Object>) choices.get(0).get("message");
                    if (messageObj != null && messageObj.containsKey("content")) {
                        return messageObj.get("content").toString().trim();
                    }
                }
            }

            return "AI returned empty response.";

        } catch (ResourceAccessException e) {
            return "⚠️ Cannot connect to Groq server.";
        } catch (Exception e) {
            return "⚠️ AI server error: " + e.getMessage();
        }
    }
}