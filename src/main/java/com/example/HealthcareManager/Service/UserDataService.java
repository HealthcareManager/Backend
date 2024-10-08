package com.example.HealthcareManager.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.HealthcareManager.DTO.ExerciseLogDTO;
import com.example.HealthcareManager.DTO.HabitDTO;
import com.example.HealthcareManager.DTO.HealthDataDTO;
import com.example.HealthcareManager.Model.User;
import com.example.HealthcareManager.Repository.AccountRepository;
import com.example.HealthcareManager.Repository.ExerciseLogDTORepository;
import com.example.HealthcareManager.Repository.HealthDataDTORepository;
import com.example.HealthcareManager.Repository.UserHabitDTORepository;
import java.util.Optional;

@Service
public class UserDataService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private HealthDataDTORepository healthDataDTORepository;

    @Autowired
    private ExerciseLogDTORepository exerciseLogDTORepository;

    @Autowired
    private UserHabitDTORepository userHabitDTORepository;

    public List<Object> getUserDataList(String userId) {
        List<Object> userAllDataList = new ArrayList<>();
    
        // 查找用户信息，处理 Optional<User> 的情况
        Optional<User> userOptional = accountRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            userAllDataList.add(user);
        } else {
            // 若用户不存在，可以选择添加 null 或返回空列表
            userAllDataList.add(null); // 或者 userAllDataList.add("User not found");
        }
    
        // 查找习惯信息
        HabitDTO habitDTO = userHabitDTORepository.findHabitDTOByUserId(userId);
        if (habitDTO != null) {
            userAllDataList.add(habitDTO);
        } else {
            userAllDataList.add(null);
        }
    
        Pageable excerisepageable = PageRequest.of(0, 2);
        List<ExerciseLogDTO> exceriseDataList = exerciseLogDTORepository.findExerciseLogDTOByUserId(userId, excerisepageable);
        if (!exceriseDataList.isEmpty()) {
            userAllDataList.add(exceriseDataList);
        } else {
            userAllDataList.add(null); 
        }
    
        // 查找健康数据，分页处理
        Pageable pageable = PageRequest.of(0, 10);
        List<HealthDataDTO> healthDataList = healthDataDTORepository.findByUserId(userId, pageable);
        if (!healthDataList.isEmpty()) {
            userAllDataList.add(healthDataList);
        } else {
            userAllDataList.add(null);
        }
    
        return userAllDataList;
    }
    

    public Map<String, Object> getAllUserDataList() {
        Map<String, Object> allUserDataMap = new HashMap<>();
        
        // 獲取所有用戶
        List<User> userList = accountRepository.findAll();
        for (User user : userList) {
            String userId = user.getId();
            Map<String, Object> userDataMap = new HashMap<>();
    
            // 收集用戶的基本數據
            userDataMap.put("id", user.getId());
            userDataMap.put("username", user.getUsername());
            userDataMap.put("password", user.getPassword());
            userDataMap.put("phoneNumber", user.getPhoneNumber());
    
            // 檢查 dateOfBirth 是否為 null
            LocalDate dateOfBirth = user.getDateOfBirth();
            if (dateOfBirth != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                userDataMap.put("dateOfBirth", dateOfBirth.format(formatter));
            } else {
                // 如果 dateOfBirth 為 null，則回傳空字串或其他預設值
                userDataMap.put("dateOfBirth", "");
            }
    
            userDataMap.put("gender", user.getGender());
            userDataMap.put("role", user.getRole());
    
            // 將用戶數據加入 Map，使用用戶 ID 作為鍵
            allUserDataMap.put(userId, userDataMap);
        }
    
        return allUserDataMap;
    }
    
    
}
