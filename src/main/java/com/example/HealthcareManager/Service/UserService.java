package com.example.HealthcareManager.Service;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.example.HealthcareManager.Model.User;
import com.example.HealthcareManager.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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

    // 用戶名稱
    public boolean updateUsername(String id, String newUsername) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setUsername(newUsername);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // 密碼
    public boolean updatePassword(String id, String newPassword) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPassword(newPassword);
            userRepository.save(user);
            return true;
        }
        return false;
    }

     // 性別
     public boolean updateGender(String id, String newGender) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setGender(newGender);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // 體重
    public boolean updateWeight(String id, Double newWeight) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setWeight(newWeight);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // 身高
    public boolean updateHeight(String id, Double newHeight) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setHeight(newHeight);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
