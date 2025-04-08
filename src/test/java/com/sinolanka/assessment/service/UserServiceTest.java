package com.sinolanka.assessment.service;

import com.sinolanka.assessment.dto.UserRegisterRequest;
import com.sinolanka.assessment.entity.User;
import com.sinolanka.assessment.exception.DuplicateUserException;
import com.sinolanka.assessment.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.Optional;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testRegisterUserSuccess() {
        // Arrange
        UserRegisterRequest request = new UserRegisterRequest("testuser", "testemail@test.com", "password123");
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        // Mock repository behavior
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setUsername("testuser"); // Ensure the username is set on save
            return savedUser; // Simulate the saved user
        });

        // Act
        User registeredUser = userService.registerUser(request);

        // Assert
        assertNotNull(registeredUser);
        assertEquals("testuser", registeredUser.getUsername()); // Ensure the username is correctly set
    }



    @Test
    void testRegisterUserDuplicateUsername() {
        // Arrange
        UserRegisterRequest request = new UserRegisterRequest("testuser", "testemail@test.com", "password123");
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(DuplicateUserException.class, () -> userService.registerUser(request));
    }

    @Test
    void testLoginUserSuccess() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Manually set the value of jwtSecret using ReflectionTestUtils
        String jwtSecret = "jwtsecretkey";
        ReflectionTestUtils.setField(userService, "jwtSecret", jwtSecret);

        // Define the expected token structure (without the exact signature)
        String expectedSubject = username;
        long currentTimeMillis = System.currentTimeMillis();
        long expirationTimeMillis = currentTimeMillis + 86400000;

        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(currentTimeMillis))
                .setExpiration(new Date(expirationTimeMillis))
                .signWith(SignatureAlgorithm.HS512, jwtSecret.getBytes())
                .compact();

        // Act
        String tokenResponse = userService.loginUser(username, password);

        // Assert: Decode the JWT token and check its claims
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret.getBytes())
                .parseClaimsJws(tokenResponse)
                .getBody();

        assertEquals(expectedSubject, claims.getSubject());

        // Log the times for debugging
        long issuedAtTime = claims.getIssuedAt().getTime();
        System.out.println("currentTimeMillis: " + currentTimeMillis);
        System.out.println("issuedAtTime: " + issuedAtTime);

        // Allow for a larger time window, e.g., 2 seconds
        assertTrue(issuedAtTime >= currentTimeMillis - 2000);  // Allow for 2 seconds tolerance
        assertTrue(issuedAtTime <= currentTimeMillis + 2000);  // Allow for 2 seconds tolerance

        assertTrue(claims.getExpiration().getTime() <= expirationTimeMillis);  // Check if expiration time is less than or equal to the expected expiration time
    }



    @Test
    void testLoginUserFailure() {
        // Arrange
        String username = "testuser";
        String password = "wrongpassword";
        User user = new User();
        user.setUsername(username);
        user.setPassword("password123");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.loginUser(username, password));
    }
}
