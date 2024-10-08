package com.example.HealthcareManager.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import com.example.HealthcareManager.Model.HeightWeightRecord;
import com.example.HealthcareManager.Repository.HeightWeightRecordRepository;

@Service
public class HeightWeightRecordService {

    @Autowired
    private HeightWeightRecordRepository heightWeightRecordRepository;

    public boolean addUserData(String id, Double height, Double weight) {

        HeightWeightRecord newRecord = new HeightWeightRecord(
            id,
            height,
            weight,
            LocalDateTime.now()
        );

        heightWeightRecordRepository.save(newRecord);
        return true;
    }

    public HeightWeightRecord getData(String id){
        return heightWeightRecordRepository.findById(id).orElse(null);
    }
}
