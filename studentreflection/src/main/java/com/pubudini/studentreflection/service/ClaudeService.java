package com.pubudini.studentreflection.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class ClaudeService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com")
            .build();

    public String getInsight(String reflection, String mood, String productivity) {

        String prompt = String.format("""
                You are a compassionate and encouraging AI wellness coach for students.

                A student has submitted the following daily reflection:

                Reflection: "%s"
                Mood: %s
                Productivity: %s

                Please provide a warm, empathetic, and actionable insight (3-4 sentences max).
                Focus on emotional support, one practical tip, and an encouraging closing note.
                Keep the tone gentle, like a supportive mentor — not clinical.
                Do not use bullet points. Write in flowing, natural sentences.
                """, reflection, mood, productivity);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        try {
            Map response = webClient.post()
                    .uri("/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            List<Map<String, Object>> candidates =
                    (List<Map<String, Object>>) response.get("candidates");

            Map<String, Object> content =
                    (Map<String, Object>) candidates.get(0).get("content");

            List<Map<String, Object>> parts =
                    (List<Map<String, Object>>) content.get("parts");

            return (String) parts.get(0).get("text");

        } catch (Exception e) {
            return "🌸 Your reflection has been saved. Keep going — every step forward counts!";
        }
    }
}
