package com.pubudini.studentreflection.controller;

import com.pubudini.studentreflection.service.InsightService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/insights")
public class InsightController {

    private final InsightService insightService;

    public InsightController(InsightService insightService) {
        this.insightService = insightService;
    }

    @GetMapping("/burnout")
    public ResponseEntity<Map<String, String>> getBurnoutWarning(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(insightService.getBurnoutWarning(userDetails.getUsername()));
    }

    @GetMapping("/suggestions")
    public ResponseEntity<Map<String, String>> getSuggestions(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(insightService.getPersonalizedSuggestions(userDetails.getUsername()));
    }

    @GetMapping("/weekly-summary")
    public ResponseEntity<Map<String, String>> getWeeklySummary(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(insightService.getWeeklySummary(userDetails.getUsername()));
    }

    @GetMapping("/productivity")
    public ResponseEntity<Map<String, String>> getProductivityInsight(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(insightService.getProductivityInsight(userDetails.getUsername()));
    }

    @GetMapping("/prediction")
    public ResponseEntity<Map<String, String>> getStudyPrediction(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(insightService.getStudyPrediction(userDetails.getUsername()));
    }
}
