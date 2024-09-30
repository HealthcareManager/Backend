package com.example.HealthcareManager.DTO;

import java.time.LocalDateTime;

public class HealthDataDTO {

    private String id;
    private Integer heartRate;
    private String bloodPressure;
    private float bloodSugar;
    private float bloodOxygen;
    private LocalDateTime date;
    private double caloriesBurned;
    private double sleepDuration;
    private Integer steps;

    // 更新建構函數，包含所有必要屬性
    public HealthDataDTO(String id, Integer heartRate, String bloodPressure, float bloodSugar, float bloodOxygen,
            LocalDateTime date, double caloriesBurned, double sleepDuration, Integer steps) {
        this.id = id;
        this.heartRate = heartRate;
        this.bloodPressure = bloodPressure;
        this.bloodSugar = bloodSugar;
        this.bloodOxygen = bloodOxygen;
        this.date = date;
        this.caloriesBurned = caloriesBurned;
        this.sleepDuration = sleepDuration;
        this.steps = steps;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public float getBloodSugar() {
        return bloodSugar;
    }

    public void setBloodSugar(float bloodSugar) {
        this.bloodSugar = bloodSugar;
    }

    public float getBloodOxygen() {
        return bloodOxygen;
    }

    public void setBloodOxygen(float bloodOxygen) {
        this.bloodOxygen = bloodOxygen;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Double getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(double caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public double getSleepDuration() {
        return sleepDuration;
    }

    public void setSleepDuration(double sleepDuration) {
        this.sleepDuration = sleepDuration;
    }

    public Integer getSteps() {
        return steps;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }
}


