package com.example.HealthcareManager.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.HealthcareManager.Model.ExerciseLog;

@Repository
public interface ExerciseLogRepository extends JpaRepository<ExerciseLog, Long> {
    // 你可以在這裡定義自訂的查詢方法，如果需要
    List<ExerciseLog> findByUserId(String userId);
}
