package com.pubudini.studentreflection.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class ClaudeService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com")
            .build();

    // --- AI Insight (existing) ---
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

        return callGemini(prompt, 300);
    }

    // --- Sentiment Analysis (NEW) ---
    public Map<String, String> analyzeSentiment(String reflection) {
        String prompt = String.format("""
                Analyze the following student journal entry.
                Respond ONLY in this exact format, nothing else:
                SENTIMENT: Positive
                EMOTION: Joy
                STRESS: Low

                Use ONLY these exact values:
                SENTIMENT must be one of: Positive, Neutral, Negative
                EMOTION must be one of: Joy, Calm, Sadness, Fear, Anger, Stress
                STRESS must be one of: Low, Medium, High

                Journal entry: "%s"
                """, reflection);

        String raw = callGemini(prompt, 50);

        Map<String, String> result = new HashMap<>();
        result.put("sentiment", "Neutral");
        result.put("emotion", "Calm");
        result.put("stressLevel", "Low");

        try {
            for (String line : raw.split("\n")) {
                line = line.trim();
                if (line.startsWith("SENTIMENT:")) {
                    result.put("sentiment", line.replace("SENTIMENT:", "").trim());
                } else if (line.startsWith("EMOTION:")) {
                    result.put("emotion", line.replace("EMOTION:", "").trim());
                } else if (line.startsWith("STRESS:")) {
                    result.put("stressLevel", line.replace("STRESS:", "").trim());
                }
            }
        } catch (Exception e) {
            // defaults already set above
        }

        return result;
    }

    // --- Multilingual insight ---
    public String getInsightMultilingual(String reflection, String mood,
                                         String productivity, String language) {
        String langInstruction = language.equals("Sinhala")
                ? "The student wrote in Sinhala. Please respond in both Sinhala and English."
                : "Respond in English.";

        String prompt = String.format("""
                You are a compassionate AI wellness coach for students.
                %s

                Reflection: "%s"
                Mood: %s
                Productivity: %s

                Provide a warm, empathetic insight (3-4 sentences).
                Focus on emotional support, one practical tip, and encouragement.
                Do not use bullet points. Write in flowing sentences.
                """, langInstruction, reflection, mood, productivity);

        return callGemini(prompt, 400);
    }

    // --- Language detection ---
    public String detectLanguage(String text) {
        String prompt = String.format("""
                Detect the language of this text and respond with ONLY one word.
                If it is Sinhala (or Sinhala mixed with English), respond: Sinhala
                If it is English, respond: English
                If it is mixed, respond: Mixed

                Text: "%s"
                """, text);

        try {
            return callGemini(prompt, 10).trim();
        } catch (Exception e) {
            return "English";
        }
    }

    // --- Public wrapper for InsightService ---
    public String callGeminiPublic(String prompt, int maxTokens) {
        return callGemini(prompt, maxTokens);
    }

    // --- Shared Gemini caller ---
    private String callGemini(String prompt, int maxTokens) {
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                ),
                "generationConfig", Map.of("maxOutputTokens", maxTokens)
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
            return "🌸 Your reflection has been saved. Keep going!";
        }
    }
}