package com.example.HealthcareManager.Model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;
    private String userId; // 可設定為null，等到支付成功後再寫入
    private String status;
    private LocalDateTime paymentTime;

    // Constructors
    public Payment() {}

    public Payment(String orderId, String userId, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.status = status;
    }
    
    public Payment(String orderId, String userId, String status, LocalDateTime paymentTime) {
        this.orderId = orderId;
        this.userId = userId;
        this.status = status;
        this.paymentTime = paymentTime;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }
    
    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }
}
