package com.sinolanka.assessment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoginHistoryResponse {
    private LocalDateTime timestamp;
    private boolean success;
}