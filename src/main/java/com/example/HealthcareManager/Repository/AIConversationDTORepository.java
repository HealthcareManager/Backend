package com.example.HealthcareManager.Repository;

import com.example.HealthcareManager.DTO.AIConversationDTO;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.HealthcareManager.Model.AIConversation;
import java.util.List;

public interface AIConversationDTORepository extends JpaRepository<AIConversation, Integer> {

    @Query("SELECT new com.example.HealthcareManager.DTO.AIConversationDTO(u.id, a.question, a.answer, a.createdAt) " +
            "FROM User u JOIN AIConversation a ON u.id = a.user.id " +
            "WHERE u.id = :userId " +
            "ORDER BY a.createdAt DESC")
    List<AIConversationDTO> AIConversationHistory(@Param("userId") String userId, Pageable pageable);
}

