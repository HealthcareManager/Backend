package com.example.HealthcareManager.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "record")
public class HeightWeightRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增的唯一主键
    private Long recordId;

    private String userId;

    private Double height;
    private Double weight;
    private LocalDateTime date;
    
    public HeightWeightRecord() {
    }

    public HeightWeightRecord(Long recordId, String userId, Double height, Double weight, LocalDateTime date) {
        this.recordId = recordId;
        this.userId = userId;
        this.height = height;
        this.weight = weight;
        this.date = date;
    }

    public HeightWeightRecord(String userId, double height, double weight, LocalDateTime date) {
        this.userId = userId;
        this.height = height;
        this.weight = weight;
        this.date = date;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    
}
