package com.example.HealthcareManager.Model;

import java.util.List;

public class UserMetricsResponse {
    private String userId;
    private List<HealthMetrics> metrics;

    public UserMetricsResponse(String userId, List<HealthMetrics> metrics) {
        this.userId = userId;
        this.metrics = metrics;
    }
    //getter setter

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<HealthMetrics> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<HealthMetrics> metrics) {
        this.metrics = metrics;
    }

}