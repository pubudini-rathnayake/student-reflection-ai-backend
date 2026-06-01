package com.pubudini.studentreflection.controller;

import com.pubudini.studentreflection.dto.ReflectionRequest;
import com.pubudini.studentreflection.entity.Reflection;
import com.pubudini.studentreflection.service.ReflectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reflections")
public class ReflectionController {

    private final ReflectionService reflectionService;

    public ReflectionController(ReflectionService reflectionService) {
        this.reflectionService = reflectionService;
    }

    @PostMapping
    public ResponseEntity<Reflection> saveReflection(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ReflectionRequest request) {

        Reflection saved = reflectionService.saveReflection(
                userDetails.getUsername(), request);

        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Reflection>> getReflections(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<Reflection> reflections = reflectionService.getReflections(
                userDetails.getUsername());

        return ResponseEntity.ok(reflections);
    }
}