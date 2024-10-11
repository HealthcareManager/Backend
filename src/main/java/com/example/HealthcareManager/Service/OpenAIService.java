package com.example.HealthcareManager.Service;

import org.checkerframework.checker.units.qual.s;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

import com.example.HealthcareManager.DTO.AIConversationDTO;
import com.example.HealthcareManager.DTO.ExerciseLogDTO;
import com.example.HealthcareManager.DTO.HealthDataDTO;
import com.example.HealthcareManager.DTO.UserHabitDTO;
import com.example.HealthcareManager.Model.AIConversation;
import com.example.HealthcareManager.Model.User;
import com.example.HealthcareManager.Repository.AIConversationDTORepository;
import com.example.HealthcareManager.Repository.AIConversationRepository;
import com.example.HealthcareManager.Repository.ExerciseLogDTORepository;
import com.example.HealthcareManager.Repository.HealthDataDTORepository;
import com.example.HealthcareManager.Repository.UserHabitDTORepository;
import com.example.HealthcareManager.Repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.io.IOException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.annotation.JsonInclude;

@Service
public class OpenAIService {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey; // OpenAI API 金鑰

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HealthDataDTORepository healthDataDTORepository; // 健康數據資料庫
    @Autowired
    private UserHabitDTORepository userHabitDTORepository; // 使用者習慣資料庫
    @Autowired
    private ExerciseLogDTORepository exerciseLogDTORepository; // 運動紀錄資料庫
    @Autowired
    private AIConversationDTORepository aiConversationDTORepository; // AI對話紀錄資料庫
    @Autowired
    private AIConversationRepository aIConversationRepository;

    @SuppressWarnings("unchecked")
    public Map<String, Object> handleGeneralQuestions(String userId, String question) {
        Map<String, Object> responseJson = new HashMap<>();

        try {

            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("使用者未找到"));

            // 計算該用戶過去 24 小時內的提問數量
            LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
            int last24HoursQuestionCount = aIConversationRepository.countByUserAndCreatedAtAfter(user, twentyFourHoursAgo);

            // 根據使用者角色設定提問次數限制
            int questionLimit = user.getRole().equals("VIP") ? 20 : 3;

            // 檢查是否超過每 24 小時提問次數限制
            if (last24HoursQuestionCount >= questionLimit) {
                responseJson.put("answer", "過去24小時的提問次數已達上限");
                return responseJson;
            }

            // 設置 ObjectMapper 忽略 null 值
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // 忽略所有 null 值

            // 獲取最近10條健康數據
            Pageable pageable = PageRequest.of(0, 10);
            List<HealthDataDTO> healthDataList = healthDataDTORepository.findByUserId(userId, pageable);

            // 獲取最近2條運動紀錄
            Pageable exercisePageable = PageRequest.of(0, 2);
            List<ExerciseLogDTO> exerciseDataList = exerciseLogDTORepository.findExerciseLogDTOByUserId(userId, exercisePageable);

            // 獲取使用者習慣
            UserHabitDTO userHabit = userHabitDTORepository.findUserHabitDTObyUserId(userId);

            // 獲取最近10條對話紀錄
            Pageable conversationPageable = PageRequest.of(0, 10);
            List<AIConversationDTO> recentConversations = aiConversationDTORepository.AIConversationHistory(userId, conversationPageable);

            // 使用 ObjectMapper 來構建健康數據 JSON
            ArrayNode healthDataArray = mapper.createArrayNode();
            for (HealthDataDTO healthData : healthDataList) {
                ObjectNode healthDataNode = mapper.createObjectNode();
                healthDataNode.put("heart_rate", healthData.getHeartRate());
                healthDataNode.put("systolic_pressure", Integer.parseInt(healthData.getBloodPressure().split("/")[0])); // 分割血壓
                healthDataNode.put("diastolic_pressure", Integer.parseInt(healthData.getBloodPressure().split("/")[1]));
                healthDataNode.put("blood_sugar", healthData.getBloodSugar());
                healthDataNode.put("blood_oxygen", healthData.getBloodOxygen());
                healthDataNode.put("date", healthData.getDate() != null ? healthData.getDate().toString() : null);
                healthDataArray.add(healthDataNode);
            }

            // 使用 ObjectMapper 來構建運動數據 JSON
            ArrayNode exerciseDataArray = mapper.createArrayNode();
            for (ExerciseLogDTO exerciseLog : exerciseDataList) {
                ObjectNode exerciseLogNode = mapper.createObjectNode();
                exerciseLogNode.put("exerciseType", exerciseLog.getExerciseType());
                exerciseLogNode.put("duration", exerciseLog.getDuration());
                exerciseLogNode.put("caloriesBurned", exerciseLog.getCaloriesBurned());
                exerciseLogNode.put("kilometers", exerciseLog.getKilometers());
                exerciseLogNode.put("createdAt", exerciseLog.getCreatedAt() != null ? exerciseLog.getCreatedAt().toString() : null);
                exerciseDataArray.add(exerciseLogNode);
            }

            // 將最近的對話紀錄添加到消息中
            ArrayNode messagesArray = mapper.createArrayNode();
            for (AIConversationDTO conversation : recentConversations) {
                ObjectNode previousUserMessage = mapper.createObjectNode();
                previousUserMessage.put("role", "user");
                previousUserMessage.put("content", conversation.getQuestion());
                messagesArray.add(previousUserMessage);

                ObjectNode previousAIMessage = mapper.createObjectNode();
                previousAIMessage.put("role", "assistant");
                previousAIMessage.put("content", conversation.getAnswer());
                previousAIMessage.put("createdAt", conversation.getCreatedAt() != null ? conversation.getCreatedAt().toString() : null);
                messagesArray.add(previousAIMessage);
            }

            // 構建請求的 JSON
            ObjectNode requestBodyNode = mapper.createObjectNode();
            requestBodyNode.put("model", "gpt-4");
            ArrayNode messagesNode = mapper.createArrayNode();

            // 系統信息
            ObjectNode systemMessage = mapper.createObjectNode();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是一位專業的健康管理助理，你的任務是根據用戶的健康數據、運動紀錄、個人習慣和過去的對話紀錄來提供具體且個性化的健康建議。請根據所有的資料來量身定制建議，避免重複回應已分析過的健康數據或問題，若數據已被分析，則避免再次回答相關問題，但請參考過去的問題和回答，進行前後呼應的回應。你的回答應限制在80字以內，提供具體且針對性的建議，並根據使用者的健康狀況，決定是否需要提出就醫建議。若使用者問非健康相關問題，請回應「此問題超出我的工作範圍」。若發現健康數據異常，請主動提醒用戶，並建議定期檢查或聯繫醫生。提供健康計畫時，請具體到行動步驟，並以正面鼓勵語句結尾。請使用繁體中文回答。");
            messagesNode.add(systemMessage);

            // 處理身高和體重
            String height = (userHabit != null && userHabit.getHeight() != null) ? userHabit.getHeight().toString() : "無資料";
            String weight = (userHabit != null && userHabit.getWeight() != null) ? userHabit.getWeight().toString() : "無資料";
            String gender = (userHabit != null && userHabit.getGender() != null) ? userHabit.getGender() : "無資料";
            String dateOfBirth = (userHabit != null && userHabit.getDateOfBirth() != null) ? userHabit.getDateOfBirth().toString() : "無資料";

            // 用戶消息
            ObjectNode userMessage = mapper.createObjectNode();
            userMessage.put("role", "user");
            userMessage.put("previous conversation", messagesArray.toString());
            userMessage.put("content", "以下是我的健康數據：" + healthDataArray.toString() +
                    "，身高: " + height + "，體重: " + weight + "，性別: " + gender +
                    "，生日: " + dateOfBirth + "。我的運動數據：" + exerciseDataArray.toString() +
                    "。我的問題是：" + question + "。請根據這些資料提供健康計畫與建議。");
            messagesNode.add(userMessage);

            requestBodyNode.set("messages", messagesNode);
            String requestBody = requestBodyNode.toString();

            // 發送 HTTP 請求
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            // 發送請求並處理回應
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 打印回應的狀態碼和內容
            System.out.println("Response Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());

            // 使用 Jackson 解析回應的 JSON
            Map<String, Object> responseMap = mapper.readValue(response.body(), Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");

            if (choices == null || choices.isEmpty()) {
                throw new RuntimeException("No choices available in the response.");
            }

            String responseContent = (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");

            // 打印 AI 回應
            System.out.println("--------------" + responseContent + "--------------");

            System.out.println("Response Content Length: " + responseContent.length());
            System.out.println("Response Content: " + responseContent);

            // 記錄對話紀錄
            AIConversation aiConversation = new AIConversation(null, new User(userId), question, responseContent, LocalDateTime.now());
            aIConversationRepository.save(aiConversation);
            responseJson.put("answer", responseContent);

        } catch (Exception e) {
            // 捕捉例外，並印出詳細錯誤訊息
            e.printStackTrace();

            // 保持現有的錯誤回應
            responseJson.put("answer", "AI無法回應");
        }

        return responseJson;
    }
}

