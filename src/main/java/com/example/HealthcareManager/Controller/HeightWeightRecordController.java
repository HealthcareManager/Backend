package com.example.HealthcareManager.Controller;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.HealthcareManager.Model.HeightWeightRecord;
import com.example.HealthcareManager.Service.HeightWeightRecordService;
import com.example.HealthcareManager.Service.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/heightWeightRecord")
public class HeightWeightRecordController {

    @Autowired
    private HeightWeightRecordService heightWeightRecordService;

    @Autowired
    private UserService userService;

    @PutMapping("addData")
    public ResponseEntity<String> addData(@RequestBody Map<String, String> userData) {
        String userId = userData.get("userId");
        Double height = Double.parseDouble(userData.get("height"));
        Double weight = Double.parseDouble(userData.get("weight"));

        boolean result = heightWeightRecordService.addUserData(userId, height, weight);
        boolean result1 = userService.updateHeight(userId, height);
        boolean result2 = userService.updateWeight(userId, weight);
        if (result && result1 && result2) {
            return ResponseEntity.ok("資料上傳成功");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("資料上傳失敗");
    }

    @PostMapping("getData/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getData(@PathVariable String userId) {
        // 獲取所有符合條件的記錄
        List<HeightWeightRecord> records = heightWeightRecordService.getData(userId);

        if (!records.isEmpty()) {
            List<Map<String, Object>> responseList = new ArrayList<>();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");

            for (HeightWeightRecord record : records) {
                Map<String, Object> response = new HashMap<>();
                response.put("userId", record.getUserId()); // 確保這裡使用正確的 key
                response.put("height", record.getHeight());
                response.put("weight", record.getWeight());

                String formattedDate = record.getDate().format(formatter);
                response.put("date", formattedDate);  // 使用格式化後的日期
                
                responseList.add(response);
            }

            return ResponseEntity.ok(responseList);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
