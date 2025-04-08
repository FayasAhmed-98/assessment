package com.sinolanka.assessment.service;
import com.sinolanka.assessment.dto.LoginHistoryResponse;
import com.sinolanka.assessment.entity.LoginAttempt;
import com.sinolanka.assessment.entity.User;
import com.sinolanka.assessment.repository.LoginAttemptRepository;
import com.sinolanka.assessment.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LoginAttemptServiceTest {

    @Mock
    private LoginAttemptRepository loginAttemptRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoginAttemptService loginAttemptService;

    private User user;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername("testuser");
    }

    @Test
    void testRecordAttempt() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        LoginAttempt attempt = new LoginAttempt();
        attempt.setUser(user);
        attempt.setTimestamp(LocalDateTime.now());
        attempt.setSuccess(true);

        // Act
        loginAttemptService.recordAttempt("testuser", true);

        // Assert
        verify(loginAttemptRepository).save(any(LoginAttempt.class));
    }

    @Test
    void testGetLoginHistory() {
        // Arrange
        LoginAttempt attempt = new LoginAttempt();
        attempt.setUser(user);
        attempt.setTimestamp(LocalDateTime.now());
        attempt.setSuccess(true);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(loginAttemptRepository.findTop5ByUserOrderByTimestampDesc(user)).thenReturn(List.of(attempt));

        // Act
        List<LoginHistoryResponse> response = loginAttemptService.getLoginHistory("testuser");

        // Assert
        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
    }
}
