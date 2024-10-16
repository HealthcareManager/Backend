package com.example.HealthcareManager.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.HealthcareManager.Model.PasswordResetToken;
import com.example.HealthcareManager.Model.User;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
    List<PasswordResetToken> findByVo(User vo);
    void deleteByToken(String token);
    PasswordResetToken findByVo_Username(String username);
}