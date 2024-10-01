package com.example.HealthcareManager.Service;

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

import com.example.HealthcareManager.DTO.ExerciseLogDTO;
import com.example.HealthcareManager.DTO.HealthDataDTO;
import com.example.HealthcareManager.DTO.UserHabitDTO;
import com.example.HealthcareManager.Repository.ExerciseLogDTORepository;
import com.example.HealthcareManager.Repository.HealthDataDTORepository;
import com.example.HealthcareManager.Repository.UserHabitDTORepository;
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
    private String apiKey;

    @Autowired
    private HealthDataDTORepository healthDataDTORepository;
    @Autowired
    private UserHabitDTORepository userHabitDTORepository;
    @Autowired
    private ExerciseLogDTORepository exerciseLogDTORepository;

    @SuppressWarnings("unchecked")
    public Map<String, Object> handleGeneralQuestions(String userId, String question) {
        Map<String, Object> responseJson = new HashMap<>();
        try {
            // 設置 ObjectMapper 忽略 null 值
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // 忽略所有 null 值

            // 获取最近10条健康数据
            Pageable pageable = PageRequest.of(0, 10);
            List<HealthDataDTO> healthDataList = healthDataDTORepository.findByUserId(userId, pageable);
            Pageable excerisepageable = PageRequest.of(0, 2);
            List<ExerciseLogDTO> exerciseDataList = exerciseLogDTORepository.findExerciseLogDTOByUserId(userId,
                    excerisepageable);
            // 获取用户习惯
            UserHabitDTO userHabit = userHabitDTORepository.findUserHabitDTObyUserId(userId);

            // 使用 ObjectMapper 来构建 JSON 字符串
            ArrayNode healthDataArray = mapper.createArrayNode();
            for (HealthDataDTO healthData : healthDataList) {
                ObjectNode healthDataNode = mapper.createObjectNode();
                healthDataNode.put("heart_rate", healthData.getHeartRate());
                healthDataNode.put("systolic_pressure", Integer.parseInt(healthData.getBloodPressure().split("/")[0])); // 分割血壓
                healthDataNode.put("diastolic_pressure", Integer.parseInt(healthData.getBloodPressure().split("/")[1]));
                healthDataNode.put("blood_sugar", healthData.getBloodSugar());
                healthDataNode.put("blood_oxygen", healthData.getBloodOxygen());
                healthDataNode.put("date", healthData.getDate() != null ? healthData.getDate().toString() : null); // 使用 null 判斷
                healthDataArray.add(healthDataNode);
            }

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

            // 构建请求的 JSON
            ObjectNode requestBodyNode = mapper.createObjectNode();
            requestBodyNode.put("model", "gpt-4o");
            ArrayNode messagesNode = mapper.createArrayNode();
            ObjectNode systemMessage = mapper.createObjectNode();
            systemMessage.put("role", "system");
            systemMessage.put("content",
                    "你是一位健康管理助理，你的任務是根據用戶的健康數據提供健康建議。請使用繁體中文回應，只回答健康相關問題。如果用戶問非健康問題，請回應‘我無法回答你的問題’。");
            messagesNode.add(systemMessage);

            ObjectNode userMessage = mapper.createObjectNode();
            userMessage.put("role", "user");
            userMessage.put("content", "以下是我最近的健康數據：" + healthDataArray.toString() +
                    ", 我的身高是: " + userHabit.getHeight() +
                    ", 我的性別是: \"" + userHabit.getGender() + "\"" +
                    ", 我的生日是: \"" + userHabit.getDateOfBirth() + "\"" +
                    ", 我的習慣是：酒精: " + userHabit.isAlcohol() +
                    ", 香煙: " + userHabit.isCigarette() +
                    ", 檳榔: " + userHabit.isAreca() +
                    "。我最近的運動數據是：" + exerciseDataArray.toString() +
                    "。我的問題是：" + question +
                    "。請幫我制定一個有助於我身體健康的計畫，並給我一些鼓勵的話。請給我一些建議，關於是否需要就醫。");
            messagesNode.add(userMessage);

            requestBodyNode.set("messages", messagesNode);

            String requestBody = requestBodyNode.toString();
            System.out.println("-------------------------------" + requestBody
                    + "--------------------------------------------------");

            // 發送 HTTP 請求
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 使用 Jackson 來解析 JSON
            Map<String, Object> responseMap = mapper.readValue(response.body(), Map.class);

            System.out.println("------------------------------" + responseMap);

            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
            if (choices == null || choices.isEmpty()) {
                throw new RuntimeException("No choices available in the response.");
            }
            String responseContent = (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");

            responseJson.put("answer", responseContent);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            responseJson.put("error", "無法解析 OpenAI API 的回應，請檢查 API 請求格式。");
        } catch (IOException e) {
            e.printStackTrace();
            responseJson.put("error", "無法連接 OpenAI API，請檢查網路連線。");
        } catch (RuntimeException e) {
            e.printStackTrace();
            responseJson.put("error", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            responseJson.put("answer", "無法處理請求，請稍後再試。");
        }
        return responseJson;
    }
}
