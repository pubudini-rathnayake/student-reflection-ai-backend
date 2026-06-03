package com.pubudini.studentreflection.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reflections")
public class Reflection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String reflection;

    private String mood;
    private String productivity;

    @Column(columnDefinition = "TEXT")
    private String aiInsight;

    // NEW FIELDS
    private String sentiment;    // Positive / Neutral / Negative
    private String emotion;      // Joy / Sadness / Fear / Anger / Stress / Calm
    private String stressLevel;// Low / Medium / High
    private String detectedLanguage;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // Existing getters/setters
    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getReflection() { return reflection; }
    public void setReflection(String reflection) { this.reflection = reflection; }

    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }

    public String getProductivity() { return productivity; }
    public void setProductivity(String productivity) { this.productivity = productivity; }

    public String getAiInsight() { return aiInsight; }
    public void setAiInsight(String aiInsight) { this.aiInsight = aiInsight; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    // NEW getters/setters
    public String getSentiment() { return sentiment; }
    public void setSentiment(String sentiment) { this.sentiment = sentiment; }

    public String getEmotion() { return emotion; }
    public void setEmotion(String emotion) { this.emotion = emotion; }

    public String getStressLevel() { return stressLevel; }
    public void setStressLevel(String stressLevel) { this.stressLevel = stressLevel; }

    public String getDetectedLanguage() { return detectedLanguage; }
    public void setDetectedLanguage(String detectedLanguage) { this.detectedLanguage = detectedLanguage; }
}