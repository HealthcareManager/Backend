package com.example.HealthcareManager.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.HealthcareManager.Model.HeightWeightRecord;

@Repository
public interface HeightWeightRecordRepository extends JpaRepository<HeightWeightRecord, Long> {
    
    List<HeightWeightRecord> findAllByUserId(String userId);

}
