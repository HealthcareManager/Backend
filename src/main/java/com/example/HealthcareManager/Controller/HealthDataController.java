package com.example.HealthcareManager.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class HealthDataController {

    @GetMapping("/healthData")
    public Map<String, Object> getHealthData() {
        Random random = new Random();

        // 生成模擬數據
        int heartRate = 60 + random.nextInt(40); // 生成60到100之間的心率
        int systolic = 110 + random.nextInt(20); // 收縮壓 110-130
        int diastolic = 70 + random.nextInt(10); // 舒張壓 70-80
        int bloodSugar = 80 + random.nextInt(40); // 血糖 80-120
        int bloodOxygen = 95 + random.nextInt(5); // 血氧 95-100
        LocalDateTime timestamp = LocalDateTime.now();

        // 生成假日期
        LocalDate fakeDate = LocalDate.of(2022, random.nextInt(12) + 1, random.nextInt(28) + 1); // 假日期，隨機生成月份和天數
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 將數據組裝成 Map
        Map<String, Object> healthData = new HashMap<>();
        healthData.put("heartRate", heartRate);

        Map<String, Integer> bloodPressure = new HashMap<>();
        bloodPressure.put("systolic", systolic);
        bloodPressure.put("diastolic", diastolic);
        healthData.put("bloodPressure", bloodPressure);

        healthData.put("bloodSugar", bloodSugar);
        healthData.put("bloodOxygen", bloodOxygen);
        healthData.put("timestamp", timestamp.toString());

        return healthData;
    }
}
