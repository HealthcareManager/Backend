package com.example.HealthcareManager.DTO;

import java.time.LocalDate;

public class UserHabitDTO {
    private String id;
    private Double weight;
    private Double height;
    private String gender;
    private LocalDate dateOfBirth;
    private boolean alcohol;
    private boolean cigarette;
    private boolean areca;

    
    public UserHabitDTO(String id, Double weight, Double height, String gender, LocalDate dateOfBirth, boolean alcohol,
            boolean cigarette, boolean areca) {
        this.id = id;
        this.weight = weight;
        this.height = height;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.alcohol = alcohol;
        this.cigarette = cigarette;
        this.areca = areca;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public boolean isAlcohol() {
        return alcohol;
    }

    public void setAlchol(boolean alcohol) {
        this.alcohol = alcohol;
    }

    public boolean isCigarette() {
        return cigarette;
    }

    public void setCigarette(boolean cigarette) {
        this.cigarette = cigarette;
    }

    public boolean isAreca() {
        return areca;
    }

    public void setAreca(boolean areca) {
        this.areca = areca;
    }
}
