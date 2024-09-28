package com.example.HealthcareManager.Repository;

import com.example.HealthcareManager.Model.HealthMetrics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthMetricsRepository extends JpaRepository<HealthMetrics, Long> {
    // 這裡可以添加自定義查詢方法，例如查詢特定用戶的數據
}
