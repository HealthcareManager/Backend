package com.example.HealthcareManager.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.HealthcareManager.Model.User;
import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, String> {

    //上傳圖片路徑
    Optional<User> findById(String id);

    Optional<User> findByEmail(String email); // 自定義查詢方法

    Optional<User> findByUsername(String username);
}
