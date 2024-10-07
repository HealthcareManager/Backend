package com.example.HealthcareManager.DTO;

import com.example.HealthcareManager.Model.User;

public class UserResponse {
    private User user; // 用戶信息
    private String jwtToken; // JWT token

    public UserResponse(User user, String jwtToken) {
        this.user = user;
        this.jwtToken = jwtToken;
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
