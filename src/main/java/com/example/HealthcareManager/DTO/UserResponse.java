package com.example.HealthcareManager.DTO;

import com.example.HealthcareManager.Model.User;

public class UserResponse {
    private String id;
    private String username;
    private String email;
    private String Imagelink;
    private User user; // 用戶信息
    private String jwtToken; // JWT token
    private String role;

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

//    public UserResponse(String id, String username, String email,String Imagelink, String jwtToken){
//        this.id = id;
//        this.username = username;
//        this.email = email;
//        this.Imagelink = Imagelink;
//        this.jwtToken = jwtToken;
//    }


    public UserResponse(String id, String username, String email, String Imagelink, String role, String jwtToken) {
    	this.id = id;
        this.username = username;
        this.email = email;
        this.Imagelink = Imagelink;
        this.jwtToken = jwtToken;
        this.role = role;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImagelink() {
        return Imagelink;
    }

    public void setImagelink(String imagelink) {
        Imagelink = imagelink;
    }
}
