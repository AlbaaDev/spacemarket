package com.org.back.dto.User;

public class UserLoginResponseDto {
    private String token;
    private long expiresIn;

    public String getToken() {
        return token;
    }

    public UserLoginResponseDto setToken(String token) {
        this.token = token;
        return this;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public UserLoginResponseDto setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }
} 