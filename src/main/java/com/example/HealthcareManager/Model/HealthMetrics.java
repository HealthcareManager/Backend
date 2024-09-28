package com.example.HealthcareManager.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_metrics")
public class HealthMetrics {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 使用原本的 User 類

    private LocalDateTime date;
    private Integer steps;
    private Integer heartRate;
    private String bloodPressure;
    private Double caloriesBurned;
    private Double sleepDuration;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

    // 無參構造函數
    public HealthMetrics() {
    }

    // 帶參構造函數
    public HealthMetrics(User user, Integer heartRate, Integer steps, String bloodPressure, Double caloriesBurned, Double sleepDuration, LocalDateTime date) {
        this.user = user;
        this.heartRate = heartRate;
        this.steps = steps;
        this.bloodPressure = bloodPressure;
        this.caloriesBurned = caloriesBurned;
        this.sleepDuration = sleepDuration;
        this.date = date;
        this.createdAt = LocalDateTime.now();
    }

    // Getters 和 Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getSteps() {
        return steps;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public Double getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(Double caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public Double getSleepDuration() {
        return sleepDuration;
    }

    public void setSleepDuration(Double sleepDuration) {
        this.sleepDuration = sleepDuration;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
