package com.example.HealthcareManager.Controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Map<String, Object>> askHealthQuestion(@PathVariable String userId,
            @RequestBody Map<String, String> request) {

        String userQuestion = request.get("question");
        System.out.println(userQuestion);

        // 调用 OpenAIService 并传入用户 ID
        Map<String, Object> response = openAIService.handleGeneralQuestions(userId, userQuestion);

        // 检查响应是否包含错误信息
        if (response.containsKey("error")) {
            return ResponseEntity.badRequest().body(response);
        }

        // 只返回 answer 字段
        return ResponseEntity.ok(Collections.singletonMap("answer", response.get("answer")));
    }
}

