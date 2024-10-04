package com.example.HealthcareManager.Repository;

import com.example.HealthcareManager.Model.HealthMetrics;
import com.example.HealthcareManager.Model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthMetricsRepository extends JpaRepository<HealthMetrics, Long> {
    // 這裡可以添加自定義查詢方法，例如查詢特定用戶的數據
    List<HealthMetrics> findByUserOrderByDateAsc(User user);
    List<HealthMetrics> findByUserId(String userId);
  // 按時間戳排序查詢用戶數據
}
