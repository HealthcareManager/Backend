package com.example.HealthcareManager.DTO;

import java.time.LocalDateTime;

public class ExerciseLogDTO {

    private String id;
    private String exerciseType;
    private Double duration;
    private Double caloriesBurned;
    private float kilometers;
    private LocalDateTime createdAt;

    public ExerciseLogDTO(String id, String exerciseType, Double duration, Double caloriesBurned, float kilometers,
            LocalDateTime createdAt) {
        this.id = id;
        this.exerciseType = exerciseType;
        this.duration = duration;
        this.caloriesBurned = caloriesBurned;
        this.kilometers = kilometers;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

}
