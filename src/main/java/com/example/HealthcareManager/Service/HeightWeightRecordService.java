package com.example.HealthcareManager.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import com.example.HealthcareManager.Model.HeightWeightRecord;
import com.example.HealthcareManager.Repository.HeightWeightRecordRepository;

@Service
public class HeightWeightRecordService {

    @Autowired
    private HeightWeightRecordRepository heightWeightRecordRepository;

    public boolean addUserData(String userId, Double height, Double weight) {

        HeightWeightRecord newRecord = new HeightWeightRecord(
            userId,
            height,
            weight,
            LocalDateTime.now()
        );

        heightWeightRecordRepository.save(newRecord);
        return true;
    }

    public List<HeightWeightRecord> getData(String userId) {
        return heightWeightRecordRepository.findAllByUserId(userId);
    }
    
}
