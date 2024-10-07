package com.example.HealthcareManager.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import com.example.HealthcareManager.Model.User;
import com.example.HealthcareManager.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 上傳圖片
    public void saveImagePath(String id, String imagePath) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setImagelink(imagePath); // 假設你的User實體有一個setImageLink的方法
        userRepository.save(user);
    }

    // 根據用戶名獲取用戶資料
    public User getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    // 姓名
    public boolean updateUsername(String id, String newUsername) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setUsername(newUsername);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // 密碼
    public String updatePassword(String userId, String oldPassword, String newPassword) {
        // 根據 userId 查找用戶
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // 使用 passwordEncoder 檢查舊密碼是否匹配
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                // 如果舊密碼正確，更新新密碼
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return "success";
            } else {
                return "incorrect"; // 舊密碼不正確
            }
        }
        return "not_found"; // 用戶不存在
    }
    

    // 性別
    public boolean updateGender(String id, String newGender) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setGender(newGender);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // 身高
    public boolean updateHeight(String id, Double newHeight) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setHeight(newHeight);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // 體重
    public boolean updateWeight(String id, Double newWeight) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setWeight(newWeight);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // 綜合更新資料的方法
    public boolean updateUserData(String id, Map<String, Object> updates) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            return false;
        }

        User user = userOptional.get();

        // 更新用戶名
        if (updates.containsKey("username")) {
            String newUsername = (String) updates.get("username");
            if (userRepository.findByUsername(newUsername).isPresent()) {
                return false; // 用戶名已被使用
            }
            user.setUsername(newUsername);
        }

        // 更新性別
        if (updates.containsKey("gender")) {
            String newGender = (String) updates.get("gender");
            user.setGender(newGender);
        }

        // 更新身高
        if (updates.containsKey("height")) {
            Double newHeight = Double.parseDouble(updates.get("height").toString());
            user.setHeight(newHeight);
        }

        // 更新體重
        if (updates.containsKey("weight")) {
            Double newWeight = Double.parseDouble(updates.get("weight").toString());
            user.setWeight(newWeight);
        }

        // 保存更新後的用戶資料
        userRepository.save(user);
        return true;
    }
}
