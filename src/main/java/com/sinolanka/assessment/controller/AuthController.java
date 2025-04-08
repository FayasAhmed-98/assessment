package com.sinolanka.assessment.controller;

import com.sinolanka.assessment.dto.LoginHistoryResponse;
import com.sinolanka.assessment.dto.LoginRequest;
import com.sinolanka.assessment.dto.UserRegisterRequest;
import com.sinolanka.assessment.entity.User;
import com.sinolanka.assessment.service.LoginAttemptService;
import com.sinolanka.assessment.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final LoginAttemptService loginAttemptService;

    public AuthController(UserService userService, LoginAttemptService loginAttemptService) {
        this.userService = userService;
        this.loginAttemptService = loginAttemptService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserRegisterRequest request) {
        User user = userService.registerUser(request);
        if (user == null) {
            // Log or handle the case when user is null
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        try {
            String token = userService.loginUser(request.getUsername(), request.getPassword());
            loginAttemptService.recordAttempt(request.getUsername(), true);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            loginAttemptService.recordAttempt(request.getUsername(), false);
            return ResponseEntity.status(401).body("Login failed");
        }
    }

    @GetMapping("/history/{username}")
    public ResponseEntity<List<LoginHistoryResponse>> getLoginHistory(@PathVariable String username, Authentication authentication) {
        if (!authentication.getName().equals(username)) {
            System.out.println("Authenticated user = " + authentication.getName());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(loginAttemptService.getLoginHistory(username));
    }

}