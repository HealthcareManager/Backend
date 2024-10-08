package com.example.HealthcareManager.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "record")
public class HeightWeightRecord {
    
    @Id
    private String id;

    private double height;
    private double weight;
    private LocalDateTime date;
    
    public HeightWeightRecord() {
    }

    public HeightWeightRecord(String id, double height, double weight, LocalDateTime date) {
        this.id = id;
        this.height = height;
        this.weight = weight;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    
}
