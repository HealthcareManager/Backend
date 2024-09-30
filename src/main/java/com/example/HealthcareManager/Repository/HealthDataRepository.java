package com.example.HealthcareManager.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import com.example.HealthcareManager.Model.HealthMetrics;

// @Repository
// public interface HealthDataRepository extends JpaRepository<HealthMetrics, Long> {

//     @Query("SELECT h FROM HealthMetrics h ORDER BY h.createdAt DESC")
//     Optional<HealthMetrics> findById(String id);
// }
