package com.example.HealthcareManager.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import com.example.HealthcareManager.Model.User;

public interface AccountRepository extends JpaRepository<User, Long> {
    // 根據用戶名查詢帳戶
    Optional<User> findByUsername(String username);
    // 根據電子郵件查詢帳戶
    Optional<User> findByEmail(String email);
    // 根據驗證 token 查詢帳戶
    Optional<User> findByVerificationToken(String verificationToken);
    
    Optional<User> findById(String uniqueId);
    
//    @Query("SELECT a.imagelink FROM user a WHERE a.username = :username")
//    String findImageLinkByUsername(@Param("username") String username);

    //查詢作者名稱
    List<User> findByUsernameContainingIgnoreCase(String username);
}

