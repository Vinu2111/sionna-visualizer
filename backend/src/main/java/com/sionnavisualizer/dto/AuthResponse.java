package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class AuthResponse {
    @NotBlank
    private String token;
    @NotBlank
    private String username;
    @NotBlank
    private String message;

    @NotBlank

    private String refreshToken;

    public AuthResponse(String token, String refreshToken, String username, String message) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.username = username;
        this.message = message;
    }

    public String getToken() { return token; }
    public String getRefreshToken() { return refreshToken; }
    public String getUsername() { return username; }
    public String getMessage() { return message; }
}
