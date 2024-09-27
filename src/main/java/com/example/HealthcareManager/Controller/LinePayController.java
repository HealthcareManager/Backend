package com.example.HealthcareManager.Controller;


import com.example.HealthcareManager.Service.LinePayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/linepay")
public class LinePayController {

    @Autowired
    private LinePayService linePayService;

    // 發起支付請求
    @PostMapping("/request")
    public String requestPayment(@RequestParam int amount, @RequestParam String currency, @RequestParam String orderId) {
        // 調用 LinePayService 發送支付請求
        return linePayService.requestPayment(amount, currency, orderId);
    }
    

    // 處理支付成功的回調
    @GetMapping("/confirm")
    public String confirmPayment(@RequestParam String transactionId, @RequestParam int amount, @RequestParam String currency) {
        // 調用 LinePayService 確認支付狀態
        return linePayService.confirmPayment(transactionId, amount, currency);
    }

    // 處理支付取消的回調
    @GetMapping("/cancel")
    public String cancelPayment() {
        return "支付已取消";
    }
}