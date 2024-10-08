package com.example.HealthcareManager.DTO;

import com.example.HealthcareManager.Model.User;

public class UserResponse {
    private String id;
    private String username;
    private String Imagelink;
    private User user; // 用戶信息
    private String jwtToken; // JWT token

    public UserResponse(User user, String jwtToken) {
        this.user = user;
        this.jwtToken = jwtToken;
    }

    public UserResponse(String id, String username, String Imagelink, String jwtToken){
        this.id = id;
        this.username = username;
        this.Imagelink = Imagelink;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImagelink() {
        return Imagelink;
    }

    public void setImagelink(String imagelink) {
        Imagelink = imagelink;
    }
    
}
