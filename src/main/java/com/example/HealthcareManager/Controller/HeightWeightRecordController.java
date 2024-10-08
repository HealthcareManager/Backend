package com.example.HealthcareManager.Controller;

import java.util.Map;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.HealthcareManager.Model.HeightWeightRecord;
import com.example.HealthcareManager.Service.HeightWeightRecordService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/heightWeightRecord")
public class HeightWeightRecordController {
    
    @Autowired
    private HeightWeightRecordService heightWeightRecordService;

    @PutMapping("addData")
    public ResponseEntity<String> addData(@RequestBody Map<String, String> userData) {
        String id = userData.get("userId");
        Double height = Double.parseDouble(userData.get("height"));
        Double weight = Double.parseDouble(userData.get("weight"));

        boolean result = heightWeightRecordService.addUserData(id, height, weight);
        if (result) {
            return ResponseEntity.ok("資料上傳成功");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("資料上傳失敗");
    }
    
    @PostMapping("getData/{id}")
    public ResponseEntity<Map<String, Object>> getData(@PathVariable String id) {
        HeightWeightRecord heightWeightRecord = heightWeightRecordService.getData(id);
        if (heightWeightRecord != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("useId", heightWeightRecord.getId());
            response.put("height", heightWeightRecord.getHeight());
            response.put("weight", heightWeightRecord.getWeight());
            response.put("date", heightWeightRecord.getDate());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
}
