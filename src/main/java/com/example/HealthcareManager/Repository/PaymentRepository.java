package com.example.HealthcareManager.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.HealthcareManager.Model.Payment;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> { // 改為 Long
    Optional<Payment> findByOrderId(String orderId);
}
