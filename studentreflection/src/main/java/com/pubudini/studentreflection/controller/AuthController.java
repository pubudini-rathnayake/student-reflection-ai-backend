package com.pubudini.studentreflection.controller;

import com.pubudini.studentreflection.dto.LoginRequest;
import com.pubudini.studentreflection.dto.RegisterRequest;
import com.pubudini.studentreflection.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        String result = userService.register(request);

        if (result.equals("EMAIL_EXISTS")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already registered"));
        }

        return ResponseEntity.ok(Map.of("token", result));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String token = userService.login(request);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
        }
    }
}