package com.sinolanka.assessment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Password is required")
    private String password;

    // Constructor to initialize fields
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
