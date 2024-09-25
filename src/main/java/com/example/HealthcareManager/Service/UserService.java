package com.example.HealthcareManager.Service;

import java.io.File;
import java.io.IOException;

import com.example.HealthcareManager.Model.User;
import com.example.HealthcareManager.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    private UserRepository userRepository;

    // 上傳圖片
    public void saveImagePath(Long id, String imagePath) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setImagelink(imagePath); // 假設你的User實體有一個setImageLink的方法
        userRepository.save(user);
    }

    // 根據用戶名獲取用戶資料
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

}
