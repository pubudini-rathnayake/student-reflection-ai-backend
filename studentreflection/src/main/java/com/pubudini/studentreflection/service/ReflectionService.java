package com.pubudini.studentreflection.service;

import com.pubudini.studentreflection.dto.ReflectionRequest;
import com.pubudini.studentreflection.entity.Reflection;
import com.pubudini.studentreflection.entity.User;
import com.pubudini.studentreflection.repository.ReflectionRepository;
import com.pubudini.studentreflection.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReflectionService {

    private final ReflectionRepository reflectionRepository;
    private final UserRepository userRepository;
    private final ClaudeService claudeService;

    public ReflectionService(ReflectionRepository reflectionRepository,
                             UserRepository userRepository,
                             ClaudeService claudeService) {
        this.reflectionRepository = reflectionRepository;
        this.userRepository = userRepository;
        this.claudeService = claudeService;
    }

    public Reflection saveReflection(String email, ReflectionRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Call 1: AI insight
        String insight = claudeService.getInsight(
                request.getReflection(),
                request.getMood(),
                request.getProductivity()
        );

        // Call 2: Sentiment analysis (NEW)
        Map<String, String> analysis = claudeService.analyzeSentiment(
                request.getReflection()
        );

        Reflection reflection = new Reflection();
        reflection.setUser(user);
        reflection.setReflection(request.getReflection());
        reflection.setMood(request.getMood());
        reflection.setProductivity(request.getProductivity());
        reflection.setAiInsight(insight);

        // Save sentiment fields (NEW)
        reflection.setSentiment(analysis.getOrDefault("sentiment", "Neutral"));
        reflection.setEmotion(analysis.getOrDefault("emotion", "Calm"));
        reflection.setStressLevel(analysis.getOrDefault("stressLevel", "Low"));

        return reflectionRepository.save(reflection);
    }

    public List<Reflection> getReflections(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return reflectionRepository.findByUserOrderByCreatedAtDesc(user);
    }
}