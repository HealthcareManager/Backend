package com.example.HealthcareManager.Service;

import com.example.HealthcareManager.Model.HealthMetrics;
import com.example.HealthcareManager.Model.User;
import com.example.HealthcareManager.Repository.HealthMetricsRepository;
import com.example.HealthcareManager.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class HealthMetricsService {
    
    @Autowired
    private final HealthMetricsRepository healthMetricsRepository;
    @Autowired
    private final UserRepository userRepository;  // 用於查詢所有用戶
    private final Random random = new Random();

    public HealthMetricsService(HealthMetricsRepository healthMetricsRepository, UserRepository userRepository) {
        this.healthMetricsRepository = healthMetricsRepository;
        this.userRepository = userRepository;
    }

    // 每隔 60 秒自動為所有用戶生成假數據
    @Scheduled(fixedRate = 60000)
    public void generateFakeDataForAllUsers() {
        List<User> allUsers = userRepository.findAll();  // 查找所有用戶
        for (User user : allUsers) {
            generateFakeData(user);
            ensureMaxDataLimit(user);  // 檢查數據量並刪除多餘的數據
        }
    }

    // 生成一筆假數據
    public void generateFakeData(User user) {
        HealthMetrics metrics = new HealthMetrics(
            user,  // 傳遞 User 對象
            random.nextInt(40) + 60,  // 心率 (60 到 100 之間)
            random.nextInt(10000) + 1000,  // 步數 (1000 到 11000 之間)
            random.nextInt(40) + 90 + "/" + (random.nextInt(20) + 60),  // 血壓 (90/60 到 130/80 之間)
            random.nextFloat() * 50 + 70,  // 血糖 (70 到 120 之間)
            random.nextFloat() * 5 + 95,  // 血氧 (95% 到 100%)
            random.nextDouble() * 500 + 100,  // 卡路里消耗 (100 到 600 之間)
            random.nextDouble() * 8,  // 睡眠時間 (0 到 8 小時)
            LocalDateTime.now()  // 當前時間
        );
        healthMetricsRepository.save(metrics);
    }
    

    // 確保數據最多保留 10 筆
    public void ensureMaxDataLimit(User user) {
        List<HealthMetrics> userMetrics = healthMetricsRepository.findByUserOrderByDateAsc(user);
        if (userMetrics.size() > 10) {
            // 刪除最舊的數據
            healthMetricsRepository.delete(userMetrics.get(0));
        }
    }
}
