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
    private User user;
    
    @Column(name = "heart_rate")
    private Integer heartRate;

    @Column(name = "blood_pressure")
    private String bloodPressure; 

    @Column(name = "blood_sugar")
    private Float bloodSugar; // 使用 Float 以保持一致性

    @Column(name = "blood_oxygen")
    private Float bloodOxygen; // 使用 Float 以保持一致性

    @Column(name = "calories_burned")
    private Double caloriesBurned;
    
    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "sleep_duration")
    private Double sleepDuration;

    @Column(name = "steps")
    private Integer steps;

    // 無參構造函數
    public HealthMetrics() {
    }

    // 帶參構造函數
    public HealthMetrics(User user, Integer heartRate, Integer steps, String bloodPressure, 
                         Float bloodSugar, Float bloodOxygen, Double caloriesBurned, Double sleepDuration, LocalDateTime date) {
        this.user = user;
        this.heartRate = heartRate;
        this.steps = steps;
        this.bloodPressure = bloodPressure;
        this.bloodSugar = bloodSugar;
        this.bloodOxygen = bloodOxygen;
        this.caloriesBurned = caloriesBurned;
        this.sleepDuration = sleepDuration;
        this.date = date;
    }

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
    
    public Float getBloodSugar() {
        return bloodSugar;
    }

    public void setBloodSugar(Float bloodSugar) {
        this.bloodSugar = bloodSugar;
    }

    public Float getBloodOxygen() {
        return bloodOxygen;
    }

    public void setBloodOxygen(Float bloodOxygen) {
        this.bloodOxygen = bloodOxygen;
    }

    public Integer getSteps() {
        return steps;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}


