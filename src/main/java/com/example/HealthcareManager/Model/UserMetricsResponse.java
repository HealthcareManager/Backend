package com.example.HealthcareManager.Model;

import java.util.List;

public class UserMetricsResponse {
    private String userId;
    private List<Metric> metrics;

    // Constructor
    public UserMetricsResponse(String userId, List<Metric> metrics) {
        this.userId = userId;
        this.metrics = metrics;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<Metric> getMetrics() { return metrics; }
    public void setMetrics(List<Metric> metrics) { this.metrics = metrics; }

    // 定義內部的 Metric 類
    public static class Metric {
        private Long id;
        private int heartRate;
        private String bloodPressure;
        private double bloodSugar;
        private double bloodOxygen;
        private String timestamp;

        // Constructor
        public Metric(Long id, int heartRate, String bloodPressure, double bloodSugar, double bloodOxygen, String timestamp) {
            this.id = id;
            this.heartRate = heartRate;
            this.bloodPressure = bloodPressure;
            this.bloodSugar = bloodSugar;
            this.bloodOxygen = bloodOxygen;
            this.timestamp = timestamp;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public int getHeartRate() { return heartRate; }
        public void setHeartRate(int heartRate) { this.heartRate = heartRate; }

        public String getBloodPressure() { return bloodPressure; }
        public void setBloodPressure(String bloodPressure) { this.bloodPressure = bloodPressure; }

        public double getBloodSugar() { return bloodSugar; }
        public void setBloodSugar(double bloodSugar) { this.bloodSugar = bloodSugar; }

        public double getBloodOxygen() { return bloodOxygen; }
        public void setBloodOxygen(double bloodOxygen) { this.bloodOxygen = bloodOxygen; }

        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }
}
