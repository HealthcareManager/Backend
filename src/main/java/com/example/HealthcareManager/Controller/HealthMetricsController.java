package com.example.HealthcareManager.Controller;

import com.example.HealthcareManager.Model.HealthMetrics;
import com.example.HealthcareManager.Model.User;
import com.example.HealthcareManager.Model.UserMetricsResponse;
import com.example.HealthcareManager.Service.HealthMetricsService;
import com.example.HealthcareManager.Repository.HealthMetricsRepository;
import com.example.HealthcareManager.Repository.UserRepository;

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

    if (metricsList.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No metrics found for user.");
    }

    // 將 HealthMetrics 轉換為 UserMetricsResponse.Metric 並封裝到 UserMetricsResponse
    List<UserMetricsResponse.Metric> metrics = metricsList.stream().map(metric -> 
        new UserMetricsResponse.Metric(
            metric.getId(), 
            metric.getHeartRate(), 
            metric.getBloodPressure(), 
            metric.getBloodSugar(), 
            metric.getBloodOxygen(), 
            metric.getDate().toString()  // 假設使用 LocalDateTime，你可以根據具體的時間格式進行調整
        )
    ).collect(Collectors.toList());

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
