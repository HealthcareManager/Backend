package com.example.HealthcareManager.DTO;

import java.time.LocalDateTime;

public class ExerciseLogDTO {

    private Long  id;
    private String userId;  // 加入 userId
    private String exerciseType;
    private Double duration;
    private Double caloriesBurned;
    private float kilometers;
    private LocalDateTime createdAt;

    public ExerciseLogDTO(Long  id, String userId, String exerciseType, Double duration, Double caloriesBurned, float kilometers, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.exerciseType = exerciseType;
        this.duration = duration;
        this.caloriesBurned = caloriesBurned;
        this.kilometers = kilometers;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long  getId() {
        return id;
    }

    public void setId(Long  id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Double getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(Double caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public float getKilometers() {
        return kilometers;
    }

    public void setKilometers(float kilometers) {
        this.kilometers = kilometers;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    @Override
    public String toString() {
        return "ExerciseLogDTO{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", exerciseType='" + exerciseType + '\'' +
                ", duration=" + duration +
                ", caloriesBurned=" + caloriesBurned +
                ", kilometers=" + kilometers +
                ", createdAt=" + createdAt +
                '}';
    }
}
