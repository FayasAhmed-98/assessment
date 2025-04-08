package com.sinolanka.assessment.controller;

import com.sinolanka.assessment.dto.LoginHistoryResponse;
import com.sinolanka.assessment.dto.LoginRequest;
import com.sinolanka.assessment.dto.UserRegisterRequest;
import com.sinolanka.assessment.entity.User;
import com.sinolanka.assessment.repository.UserRepository;
import com.sinolanka.assessment.service.LoginAttemptService;
import com.sinolanka.assessment.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private LoginAttemptService loginAttemptService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Mock
    private UserRepository userRepository;


    @Test
    public void testRegisterUser() {
        // Arrange
        UserRegisterRequest request = new UserRegisterRequest("testuser", "test@example.com", "password");
        User expectedUser = new User("testuser", "test@example.com", "password");

        // Ensure the mock returns the expected User object
        Mockito.when(userService.registerUser(Mockito.any(UserRegisterRequest.class))).thenReturn(expectedUser);

        // Act
        ResponseEntity<User> response = authController.register(request);

        // Assert
        assertNotNull(response.getBody());  // This will now pass if the response body is not null
        assertEquals("testuser", response.getBody().getUsername());
    }

    @Test
    void testLoginSuccess() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");
        String token = "jwt_token";
        when(userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword())).thenReturn(token);

        // Act
        ResponseEntity<String> response = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(token, response.getBody());
        verify(loginAttemptService).recordAttempt(loginRequest.getUsername(), true);
    }

    @Test
    void testLoginFailed() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("testuser", "wrongpassword");
        when(userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword())).thenThrow(new RuntimeException("Invalid credentials"));

        // Act
        ResponseEntity<String> response = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Login failed", response.getBody());
        verify(loginAttemptService).recordAttempt(loginRequest.getUsername(), false);
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetLoginHistorySuccess() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");

        LoginHistoryResponse historyResponse = new LoginHistoryResponse();
        historyResponse.setTimestamp(LocalDateTime.parse("2023-04-08T10:00:00"));
        historyResponse.setSuccess(true);
        List<LoginHistoryResponse> history = List.of(historyResponse);

        when(loginAttemptService.getLoginHistory("testuser")).thenReturn(history);

        // Act
        ResponseEntity<List<LoginHistoryResponse>> response = authController.getLoginHistory("testuser", authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());

        // Adjusted comparison to ignore seconds
        assertEquals("2023-04-08T10:00", response.getBody().get(0).getTimestamp().toString().substring(0, 16));
    }


    @Test
    void testGetLoginHistoryForbidden() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("anotheruser");

        // Act
        ResponseEntity<List<LoginHistoryResponse>> response = authController.getLoginHistory("testuser", authentication);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
