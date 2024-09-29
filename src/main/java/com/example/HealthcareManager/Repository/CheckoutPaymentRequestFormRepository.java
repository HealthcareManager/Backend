package com.example.HealthcareManager.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.HealthcareManager.Model.CheckoutPaymentRequestForm;

import java.util.Optional;

public interface CheckoutPaymentRequestFormRepository extends JpaRepository<CheckoutPaymentRequestForm, Long> {
    Optional<CheckoutPaymentRequestForm> findByOrderId(String orderId);
}
