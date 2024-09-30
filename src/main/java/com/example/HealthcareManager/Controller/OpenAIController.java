package com.example.HealthcareManager.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.HealthcareManager.Service.OpenAIService;

@RestController
@RequestMapping("/api/openai")
public class OpenAIController {

    @Autowired
    private OpenAIService openAIService;

    @PostMapping("/ask/{userId}")
    public Map<String, Object> askHealthQuestion(@PathVariable String userId,
            @RequestBody Map<String, String> request) {

        String userQuestion = request.get("question");
        System.out.println(userQuestion);

        // 调用 OpenAIService 并传入用户 ID
        return openAIService.handleGeneralQuestions(userId, userQuestion);
    }

}
