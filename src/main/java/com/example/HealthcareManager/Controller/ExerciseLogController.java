package com.example.HealthcareManager.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.HealthcareManager.DTO.ExerciseLogDTO;
import com.example.HealthcareManager.Model.ExerciseLog;
import com.example.HealthcareManager.Repository.UserRepository;
import com.example.HealthcareManager.Service.ExerciseLogService;
import com.example.HealthcareManager.Model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/exercise")
public class ExerciseLogController {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private ExerciseLogService exerciseLogService;

    @PostMapping
    public ResponseEntity<?> createExerciseLog(@RequestBody ExerciseLogDTO exerciseLogDto) {
        // 驗證必需字段，打印請求體
        System.out.println("Received ExerciseLogDTO: " + exerciseLogDto);
    
        if (exerciseLogDto.getUserId() == null || exerciseLogDto.getExerciseType() == null) {
            System.out.println("Missing required fields in ExerciseLogDTO: " + exerciseLogDto);
            return ResponseEntity.badRequest().body("Missing required fields");
        }
    
        // 根據 userId 查找 User，並打印找到的用戶信息
        User user = userRepository.findById(exerciseLogDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        System.out.println("User found: " + user.getUsername());
    
        // 創建新的 ExerciseLog，並打印要保存的數據
        ExerciseLog exerciseLog = new ExerciseLog();
        exerciseLog.setUser(user);  // 設置 user
        exerciseLog.setExerciseType(exerciseLogDto.getExerciseType());
        exerciseLog.setDuration(exerciseLogDto.getDuration());
        exerciseLog.setCaloriesBurned(exerciseLogDto.getCaloriesBurned());
        exerciseLog.setKilometers(exerciseLogDto.getKilometers());
        exerciseLog.setCreatedAt(LocalDateTime.now());  // 設置創建時間
    
        System.out.println("Saving ExerciseLog: " + exerciseLog);
    
        // 保存到資料庫
        ExerciseLog savedLog = exerciseLogService.saveExerciseLog(exerciseLog);
    
        // 打印保存成功的 id
        System.out.println("ExerciseLog saved successfully with id: " + savedLog.getId());
    
        return ResponseEntity.ok(savedLog);
    }


    // 查詢所有運動紀錄
    @GetMapping
    public ResponseEntity<List<ExerciseLog>> getAllExerciseLogs() {
        List<ExerciseLog> exerciseLogs = exerciseLogService.getAllExerciseLogs();
        return ResponseEntity.ok(exerciseLogs);  // 返回 200 OK 和所有紀錄
    }

    // 根據 ID 查詢特定運動紀錄
    @GetMapping("/{id}")
    public ResponseEntity<ExerciseLog> getExerciseLogById(@PathVariable Long id) {
        Optional<ExerciseLog> exerciseLog = exerciseLogService.getExerciseLogById(id);
        return exerciseLog.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());  // 如果找到紀錄則返回，否則返回 404
    }

    // 根據 ID 刪除運動紀錄
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExerciseLogById(@PathVariable Long id) {
        exerciseLogService.deleteExerciseLogById(id);
        return ResponseEntity.noContent().build();  // 返回 204 No Content 狀態
    }
}
