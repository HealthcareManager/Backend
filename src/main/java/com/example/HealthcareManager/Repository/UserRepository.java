package com.example.HealthcareManager.Repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.HealthcareManager.Model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // 自定義查詢方法
}

