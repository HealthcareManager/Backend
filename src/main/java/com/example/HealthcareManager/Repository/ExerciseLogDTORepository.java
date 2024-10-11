package com.example.HealthcareManager.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.HealthcareManager.DTO.ExerciseLogDTO;
import com.example.HealthcareManager.Model.ExerciseLog;

import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ExerciseLogDTORepository extends JpaRepository<ExerciseLog, Long> {

    @Query("SELECT new com.example.HealthcareManager.DTO.ExerciseLogDTO(e.id, u.id, e.exerciseType, e.duration, e.caloriesBurned, e.kilometers, e.createdAt) " +
           "FROM ExerciseLog e JOIN e.user u " +
           "WHERE u.id = :userId " +
           "ORDER BY e.createdAt DESC")
    List<ExerciseLogDTO> findExerciseLogDTOByUserId(@Param("userId") String userId, Pageable pageable);
}

