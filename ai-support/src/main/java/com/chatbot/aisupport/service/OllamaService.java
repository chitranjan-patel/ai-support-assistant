package com.chatbot.aisupport.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

import java.util.HashMap;
import java.util.Map;

@Service
public class OllamaService {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";

    public String getAIResponse(String prompt) {

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

            String optimizedPrompt = """
You are an AI assistant.

Answer the user's question directly.
Do NOT give examples.
Do NOT simulate conversation.
Keep the answer simple and clear in 2-3 lines.

Question:
""" + prompt;

            Map<String, Object> request = new HashMap<>();
            request.put("model", "tinyllama:latest");
            request.put("prompt", optimizedPrompt);
            request.put("stream", false);
            request.put("temperature", 0.5);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(request, headers);

            // ✅ FIXED HERE
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            OLLAMA_URL,
                            HttpMethod.POST,
                            entity,
                            new org.springframework.core.ParameterizedTypeReference<>() {}
                    );

            if (response.getBody() != null &&
                    response.getBody().get("response") != null) {

                return response.getBody()
                        .get("response")
                        .toString()
                        .trim();
            }

            return "AI returned empty response.";

        } catch (ResourceAccessException e) {
            return "⚠️ Cannot connect to Ollama server.";
        } catch (Exception e) {
            return "⚠️ AI server error.";
        }
    }
}