package com.example.HealthcareManager.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.HealthcareManager.Model.ProductForm;

public interface ProductFormRepository extends JpaRepository<ProductForm, Long> {
    // 可以在此添加其他自定義查詢方法
}
