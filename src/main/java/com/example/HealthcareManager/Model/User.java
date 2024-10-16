package com.example.HealthcareManager.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table(name = "users")
public class User {
	@Id
    private String id;

    private String username;
    private String email;
    private String password;
    private LocalDate dateOfBirth;
    private Double height;
    private Double weight;
    private String gender;
    private String imagelink;
    private String phoneNumber;
	private String role;
    
	@Column(name = "verification_token", length = 255)
    private String verificationToken;
    
    @Column(name = "login_attempts")
    private Integer loginAttempts = 0;

    @Column(name = "account_locked")
    private Boolean accountLocked = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

	private LocalDate MembershipEndDate;
	
	private LocalDateTime tokenExpiration;
    
 public User(String id, String username, String email, String password, LocalDate dateOfBirth, Double height,
			Double weight, String gender, String imagelink, String phoneNumber, String role, String verificationToken,
			Integer loginAttempts, Boolean accountLocked, LocalDateTime createdAt, LocalDate membershipEndDate) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.dateOfBirth = dateOfBirth;
		this.height = height;
		this.weight = weight;
		this.gender = gender;
		this.imagelink = imagelink;
		this.phoneNumber = phoneNumber;
		this.role = role;
		this.verificationToken = verificationToken;
		this.loginAttempts = loginAttempts;
		this.accountLocked = accountLocked;
		this.createdAt = createdAt;
		MembershipEndDate = membershipEndDate;
	}

	// 无参构造函数
    public User() {
    }

	public User(String id) {
		this.id = id;
    }
    
	// 第一個建構子
    public User(String userId, String name, String imagelink) {
        this.id = userId;
        this.username = name;
        this.imagelink = imagelink;
    }

    // 第二個建構子，使用不同的參數類型
    public User(String userId, String name, Optional<String> email) {
        this.id = userId;
        this.username = name;
        this.email = email.orElse(null);
    }

	public User(String userId, String name, String email, String role) {
        this.id = userId;
        this.username = name;
        this.email = email;
        this.role = role;
    }

	public LocalDateTime getTokenExpiration() {
		return tokenExpiration;
	}

	public void setTokenExpiration(LocalDateTime tokenExpiration) {
		this.tokenExpiration = tokenExpiration;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}
	
	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getVerificationToken() {
		return verificationToken;
	}

	public void setVerificationToken(String verificationToken) {
		this.verificationToken = verificationToken;
	}

	public String getImagelink() {
		return imagelink;
	}

	public void setImagelink(String imagelink) {
		this.imagelink = imagelink;
	}

	public Integer getLoginAttempts() {
		return loginAttempts;
	}

	public void setLoginAttempts(Integer loginAttempts) {
		this.loginAttempts = loginAttempts;
	}

	public Boolean getAccountLocked() {
		return accountLocked;
	}

	public void setAccountLocked(Boolean accountLocked) {
		this.accountLocked = accountLocked;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public LocalDate getMembershipEndDate() {
		return MembershipEndDate;
	}

	public void setMembershipEndDate(LocalDate membershipEndDate) {
		MembershipEndDate = membershipEndDate;
	}

}
