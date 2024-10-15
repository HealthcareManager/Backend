package com.example.HealthcareManager.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.HealthcareManager.Model.ExerciseLog;
import com.example.HealthcareManager.Repository.ExerciseLogRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ExerciseLogService {

    @Autowired
    private ExerciseLogRepository exerciseLogRepository;

    // 新增一筆運動紀錄
    public ExerciseLog saveExerciseLog(ExerciseLog exerciseLog) {
        return exerciseLogRepository.save(exerciseLog);  // 使用 save 方法保存到資料庫
    }

    // 查詢所有的運動紀錄
    public List<ExerciseLog> getAllExerciseLogs() {
        return exerciseLogRepository.findAll();  // 使用 findAll 方法查詢所有資料
    }

    // 根據 ID 查詢單一運動紀錄
    public Optional<ExerciseLog> getExerciseLogById(Long id) {
        return exerciseLogRepository.findById(id);  // 使用 findById 方法查詢特定資料
    }

    // 刪除運動紀錄
    public void deleteExerciseLogById(Long id) {
        exerciseLogRepository.deleteById(id);  // 使用 deleteById 方法根據 ID 刪除資料
    }

    //ID來查詢多筆運動紀錄
    public List<ExerciseLog> getExerciseLogsByUserId(String userId) {
        return exerciseLogRepository.findByUserId(userId); // 使用 findByUserId 查詢使用者的所有運動紀錄
    }
}