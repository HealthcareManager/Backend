package com.example.HealthcareManager.DTO;

import java.time.LocalDateTime;

public class AIConversationDTO {

    private String id;
    private String question;
    private String answer;
    private LocalDateTime createdAt;
    
    public AIConversationDTO(String id, String question, String answer, LocalDateTime createdAt) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
