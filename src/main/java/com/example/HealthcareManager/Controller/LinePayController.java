package com.example.HealthcareManager.Controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.HealthcareManager.ConsumerCheck;
import com.example.HealthcareManager.Model.CheckoutPaymentRequestForm;


@RestController
@RequestMapping("/api")
public class LinePayController {

    @Autowired
    private ConsumerCheck service;



    // 發送支付請求到 Line Pay API
    @PostMapping("/payment")
    public ResponseEntity<Map<String, Object>> processPaymentRequest(@RequestBody CheckoutPaymentRequestForm requestBody) {
        // 將支付請求保存到資料庫
        service.savePaymentRequest(requestBody);
        Map<String, Object> response = service.sendPaymentRequest(requestBody);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/updatePaymentStatus")
    public ResponseEntity<String> updatePaymentStatus(@RequestBody Map<String, String> updateRequest) {
        String orderId = updateRequest.get("orderId");
        String userId = updateRequest.get("userId"); // 新增 userId
        String status = updateRequest.get("status");
    
        boolean isUpdated = service.updatePaymentStatus(orderId, userId, status); // 傳遞 userId
        if (isUpdated) {
            return ResponseEntity.ok("支付狀態更新成功");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("無法更新支付狀態");
        }
    }
}