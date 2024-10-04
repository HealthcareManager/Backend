package com.example.HealthcareManager.Controller;

import com.example.HealthcareManager.Model.HealthMetrics;
import com.example.HealthcareManager.Model.User;
import com.example.HealthcareManager.Model.UserMetricsResponse;
import com.example.HealthcareManager.Service.HealthMetricsService;
import com.example.HealthcareManager.Repository.HealthMetricsRepository;
import com.example.HealthcareManager.Repository.UserRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
public class HealthMetricsController {

    private final HealthMetricsService healthMetricsService;
    private final UserRepository userRepository;
    private final HealthMetricsRepository healthMetricsRepository;
    private final UserMetricsResponse userMetricsResponse;


    // 使用構造函數注入
    @Autowired
    public HealthMetricsController(HealthMetricsService healthMetricsService, UserRepository userRepository, HealthMetricsRepository healthMetricsRepository) {
        this.healthMetricsService = healthMetricsService;
        this.userRepository = userRepository;
        this.healthMetricsRepository = healthMetricsRepository;
        this.userMetricsResponse = null;
    }

    @PostMapping("/api/user-metrics/{userId}")
    public ResponseEntity<?> getUserMetrics(@PathVariable String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<HealthMetrics> metricsList = healthMetricsRepository.findByUserId(userId);
        System.out.println(metricsList);
        if (metricsList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No metrics found for user.");
        }

        // 将 HealthMetrics 转换为 UserMetricsResponse.Metric 并封装到 UserMetricsResponse
        List<UserMetricsResponse.Metric> metrics = metricsList.stream().map(metric -> {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            String formattedDate = metric.getDate().format(formatter);
            return new UserMetricsResponse.Metric(
                metric.getId(), 
                metric.getHeartRate(), 
                metric.getBloodPressure(), 
                metric.getBloodSugar(), 
                metric.getBloodOxygen(), 
                formattedDate  // 使用格式化的时间戳
            );
        }).collect(Collectors.toList());

        UserMetricsResponse response = new UserMetricsResponse(user.getId().toString(), metrics);
        return ResponseEntity.ok(response);
    }



    // 手動為指定用戶生成假數據的 API
    @PostMapping("/api/generate-fake-data/{userId}")
    public ResponseEntity<String> generateFakeData(@PathVariable String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // 生成假數據並檢查數據限制
        healthMetricsService.generateFakeData(user);
        healthMetricsService.ensureMaxDataLimit(user);

        return ResponseEntity.ok("Fake data generated and old data purged if necessary.");
    }

    // 如果需要，還可以添加手動觸發自動生成數據的功能
    @PostMapping("/api/generate-fake-data/all")
    public ResponseEntity<String> generateFakeDataForAllUsers() {
        healthMetricsService.generateFakeDataForAllUsers();  // 觸發所有用戶的假數據生成
        return ResponseEntity.ok("Fake data generated for all users.");
    }
}
