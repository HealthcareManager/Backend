package com.example.HealthcareManager.Repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.HealthcareManager.Model.AIConversation;
import com.example.HealthcareManager.Model.User;

@Repository
public interface AIConversationRepository extends JpaRepository<AIConversation, Integer> {

    // 查詢指定用戶從某時間之後的提問數量
    int countByUserAndCreatedAtAfter(User user, LocalDateTime createdAt);
}

