package com.sinolanka.assessment.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;

    // Constructor to initialize fields
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
