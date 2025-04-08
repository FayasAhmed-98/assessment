package com.sinolanka.assessment.service;

import com.sinolanka.assessment.dto.LoginHistoryResponse;
import com.sinolanka.assessment.entity.LoginAttempt;
import com.sinolanka.assessment.entity.User;
import com.sinolanka.assessment.repository.LoginAttemptRepository;
import com.sinolanka.assessment.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoginAttemptService {
    private final LoginAttemptRepository loginAttemptRepository;
    private final UserRepository userRepository;

    public LoginAttemptService(LoginAttemptRepository loginAttemptRepository, UserRepository userRepository) {
        this.loginAttemptRepository = loginAttemptRepository;
        this.userRepository = userRepository;
    }

    public void recordAttempt(String username, boolean success) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        LoginAttempt attempt = new LoginAttempt();
        attempt.setUser(user);
        attempt.setTimestamp(LocalDateTime.now());
        attempt.setSuccess(success);
        loginAttemptRepository.save(attempt);
    }

    public List<LoginHistoryResponse> getLoginHistory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return loginAttemptRepository.findTop5ByUserOrderByTimestampDesc(user)
                .stream()
                .map(attempt -> {
                    LoginHistoryResponse response = new LoginHistoryResponse();
                    response.setTimestamp(attempt.getTimestamp());
                    response.setSuccess(attempt.isSuccess());
                    return response;
                })
                .collect(Collectors.toList());
    }
}