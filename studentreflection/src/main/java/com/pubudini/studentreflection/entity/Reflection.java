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

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
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
}
