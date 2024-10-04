package com.example.HealthcareManager.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.HealthcareManager.Model.AIConversation;

@Repository
public interface AIConversationRepository extends JpaRepository<AIConversation, Integer> {
    
}
