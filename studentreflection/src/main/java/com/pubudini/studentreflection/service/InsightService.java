package com.pubudini.studentreflection.service;

import com.pubudini.studentreflection.entity.Reflection;
import com.pubudini.studentreflection.entity.User;
import com.pubudini.studentreflection.repository.ReflectionRepository;
import com.pubudini.studentreflection.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InsightService {

    private final ReflectionRepository reflectionRepository;
    private final UserRepository userRepository;
    private final ClaudeService claudeService;

    public InsightService(ReflectionRepository reflectionRepository,
                          UserRepository userRepository,
                          ClaudeService claudeService) {
        this.reflectionRepository = reflectionRepository;
        this.userRepository = userRepository;
        this.claudeService = claudeService;
    }

    // Get last N entries as a summary string for prompts
    private String buildHistory(String email, int limit) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Reflection> entries = reflectionRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream().limit(limit).collect(Collectors.toList());

        if (entries.isEmpty()) return "No reflections yet.";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < entries.size(); i++) {
            Reflection r = entries.get(i);
            sb.append(String.format(
                    "Entry %d: Mood=%s, Productivity=%s, Stress=%s, Emotion=%s, Sentiment=%s\nText: %s\n\n",
                    i + 1,
                    r.getMood() != null ? r.getMood() : "N/A",
                    r.getProductivity() != null ? r.getProductivity() : "N/A",
                    r.getStressLevel() != null ? r.getStressLevel() : "N/A",
                    r.getEmotion() != null ? r.getEmotion() : "N/A",
                    r.getSentiment() != null ? r.getSentiment() : "N/A",
                    r.getReflection()
            ));
        }
        return sb.toString();
    }

    public Map<String, String> getBurnoutWarning(String email) {
        String history = buildHistory(email, 7);

        String prompt = String.format("""
                You are a student wellness advisor. Analyze these recent journal entries:

                %s

                Based on stress levels, mood trends, and emotional patterns, assess burnout risk.
                Respond in 2-3 sentences. Start with a risk level: LOW RISK, MEDIUM RISK, or HIGH RISK.
                Be empathetic and supportive, not alarming.
                """, history);

        String result = claudeService.callGeminiPublic(prompt, 200);
        return Map.of("burnoutWarning", result);
    }

    public Map<String, String> getPersonalizedSuggestions(String email) {
        String history = buildHistory(email, 5);

        String prompt = String.format("""
                You are a supportive academic coach for students. Based on these recent journal entries:

                %s

                Give 3 specific, actionable study and wellness suggestions tailored to this student's
                patterns. Keep each suggestion to one sentence. Format as a numbered list 1. 2. 3.
                Be warm and encouraging.
                """, history);

        String result = claudeService.callGeminiPublic(prompt, 300);
        return Map.of("suggestions", result);
    }

    public Map<String, String> getWeeklySummary(String email) {
        String history = buildHistory(email, 7);

        String prompt = String.format("""
                You are a warm and encouraging AI mentor for a student. Here are their journal entries
                from the past week:

                %s

                Write a short motivational weekly summary (3-4 sentences). Highlight their strengths,
                acknowledge challenges, and end with genuine encouragement.
                Write like a caring mentor, not a robot.
                """, history);

        String result = claudeService.callGeminiPublic(prompt, 250);
        return Map.of("weeklySummary", result);
    }

    public Map<String, String> getProductivityInsight(String email) {
        String history = buildHistory(email, 7);

        String prompt = String.format("""
                You are a productivity coach for students. Based on these journal entries:

                %s

                Analyze the student's productivity patterns and give a 2-3 sentence insight.
                Identify their most productive patterns and one specific suggestion to improve.
                Be concise and practical.
                """, history);

        String result = claudeService.callGeminiPublic(prompt, 200);
        return Map.of("productivityInsight", result);
    }

    public Map<String, String> getStudyPrediction(String email) {
        String history = buildHistory(email, 10);

        String prompt = String.format("""
            You are a predictive wellness coach for students. Analyze these journal entries:

            %s

            Based on the stress and mood patterns you see, predict what the student might
            experience in the next few days. Give a 2-3 sentence prediction and one proactive
            tip to help them prepare. Be warm, specific, and helpful — not alarming.
            """, history);

        String result = claudeService.callGeminiPublic(prompt, 250);
        return Map.of("studyPrediction", result);
    }
}