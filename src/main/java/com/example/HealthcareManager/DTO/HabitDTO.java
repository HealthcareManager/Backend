package com.example.HealthcareManager.DTO;

public class HabitDTO {

    private String id;
    private boolean alcohol;
    private boolean cigarette;
    private boolean areca;

    public HabitDTO(String id, boolean alcohol, boolean cigarette, boolean areca) {
        this.id = id;
        this.alcohol = alcohol;
        this.cigarette = cigarette;
        this.areca = areca;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public boolean isAlcohol() {
        return alcohol;
    }
    public void setAlcohol(boolean alcohol) {
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
