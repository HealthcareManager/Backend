package com.example.HealthcareManager.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.HealthcareManager.DTO.HealthDataDTO;
import com.example.HealthcareManager.Model.HealthMetrics;

import java.util.List;

@Repository
public interface HealthDataDTORepository extends JpaRepository<HealthMetrics, Long> {

    @Query("SELECT new com.example.HealthcareManager.DTO.HealthDataDTO(u.id, h.heartRate, h.bloodPressure, h.bloodSugar, h.bloodOxygen, h.date, h.caloriesBurned, h.sleepDuration, h.steps) " +
           "FROM User u JOIN HealthMetrics h ON u.id = h.user.id " +
           "WHERE u.id = :Id " +
           "ORDER BY h.date DESC")
    List<HealthDataDTO> findByUserId(@Param("Id") String Id, Pageable pageable);
}
