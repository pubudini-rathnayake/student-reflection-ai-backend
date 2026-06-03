package com.pubudini.studentreflection.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClaudeService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com")
            .build();

    // --- Combined single call: insight + sentiment + language ---
    public Map<String, String> analyzeReflection(String reflection, String mood, String productivity) {
        String prompt = String.format("""
                You are a compassionate AI wellness coach for students.
                Analyze this student journal entry and respond in EXACTLY this format, nothing else:

                INSIGHT: (write a warm, empathetic 3-4 sentence insight here. Focus on emotional support, one practical tip, and encouragement. No bullet points.)
                SENTIMENT: Positive
                EMOTION: Joy
                STRESS: Low
                LANGUAGE: English

                Rules:
                - SENTIMENT must be one of: Positive, Neutral, Negative
                - EMOTION must be one of: Joy, Calm, Sadness, Fear, Anger, Stress
                - STRESS must be one of: Low, Medium, High
                - LANGUAGE must be one of: English, Sinhala, Mixed
                - INSIGHT should be warm and personal, like a caring mentor

                Student reflection: "%s"
                Mood: %s
                Productivity: %s
                """, reflection, mood, productivity);

        String raw = callGemini(prompt, 500);

        Map<String, String> result = new HashMap<>();
        result.put("aiInsight", "🌸 Your reflection has been saved. Keep going!");
        result.put("sentiment", "Neutral");
        result.put("emotion", "Calm");
        result.put("stressLevel", "Low");
        result.put("detectedLanguage", "English");

        try {
            String[] lines = raw.split("\n");
            StringBuilder insightBuilder = new StringBuilder();
            boolean readingInsight = false;

            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("INSIGHT:")) {
                    insightBuilder.append(line.replace("INSIGHT:", "").trim());
                    readingInsight = true;
                } else if (line.startsWith("SENTIMENT:")) {
                    readingInsight = false;
                    result.put("sentiment", line.replace("SENTIMENT:", "").trim());
                } else if (line.startsWith("EMOTION:")) {
                    readingInsight = false;
                    result.put("emotion", line.replace("EMOTION:", "").trim());
                } else if (line.startsWith("STRESS:")) {
                    readingInsight = false;
                    result.put("stressLevel", line.replace("STRESS:", "").trim());
                } else if (line.startsWith("LANGUAGE:")) {
                    readingInsight = false;
                    result.put("detectedLanguage", line.replace("LANGUAGE:", "").trim());
                } else if (readingInsight && !line.isEmpty()) {
                    insightBuilder.append(" ").append(line);
                }
            }

            if (insightBuilder.length() > 0) {
                result.put("aiInsight", insightBuilder.toString().trim());
            }

        } catch (Exception e) {
            System.err.println("❌ Parse error: " + e.getMessage());
        }

        return result;
    }

    // --- For InsightService (burnout, suggestions, etc.) ---
    public String callGeminiPublic(String prompt, int maxTokens) {
        return callGemini(prompt, maxTokens);
    }

    // --- Shared Gemini caller ---
    private String callGemini(String prompt, int maxTokens) {
        System.out.println("🔑 API Key loaded: " + (apiKey != null ? apiKey.substring(0, 8) + "..." : "NULL"));

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                ),
                "generationConfig", Map.of("maxOutputTokens", maxTokens)
        );

        try {
            Map response = webClient.post()
                    .uri("/v1beta/models/gemini-2.5-flash-lite:generateContent?key=" + apiKey)
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
            System.err.println("❌ Gemini API error: " + e.getMessage());
            e.printStackTrace();
            return "🌸 Your reflection has been saved. Keep going!";
        }
    }
}