package com.example.HealthcareManager.Service;

import com.example.HealthcareManager.Model.HealthMetrics;
import com.example.HealthcareManager.Model.User;
import com.example.HealthcareManager.Repository.HealthMetricsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class HealthMetricsService {
    
    @Autowired
    private final HealthMetricsRepository healthMetricsRepository;
    private final Random random = new Random();

    public HealthMetricsService(HealthMetricsRepository healthMetricsRepository) {
        this.healthMetricsRepository = healthMetricsRepository;
    }

    public void generateFakeData(User user) {
        HealthMetrics metrics = new HealthMetrics(
            user,  // 傳遞 User 對象
            random.nextInt(40) + 60,  // 心率
            random.nextInt(10000) + 1000,  // 步數
            random.nextInt(40) + 90 + "/" + (random.nextInt(20) + 60),  // 血壓
            random.nextDouble() * 500 + 100,  // 卡路里消耗
            random.nextDouble() * 8,  // 睡眠時間
            LocalDateTime.now()  // 當前時間
        );
        healthMetricsRepository.save(metrics);
    }
}
