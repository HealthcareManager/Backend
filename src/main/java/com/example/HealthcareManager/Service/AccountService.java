package com.example.HealthcareManager.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.HealthcareManager.Model.User;
import com.example.HealthcareManager.Repository.AccountRepository;
import com.example.HealthcareManager.Security.CustomUserDetails;
import com.example.HealthcareManager.Security.JwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import io.jsonwebtoken.io.IOException;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JwtService jwtService;

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final String CLIENT_ID = "709151275791-j69ulvv0dlajor84m9v1lfb62m2gbur0.apps.googleusercontent.com"; // 替换为您的 Google

    // 移除 @Autowired 和構造函數中的 User 注入
    public AccountService() {
    }

    public Optional<User> verifyGoogleToken(String idTokenString)
            throws GeneralSecurityException, IOException, java.io.IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String userId = payload.getSubject(); // 用户的唯一 ID
            String name = (String) payload.get("name");
            String email = payload.getEmail();
            System.out.println("userId, name, email is " + userId + name + email);

            // 检查用户是否已注册
            Optional<User> existingUser = accountRepository.findByEmail(email);
            System.out.println("existingUser is " + existingUser);
            if (existingUser == null) {
                // 用户未注册，创建新用户
                User newUser = new User(userId, name, email);
                accountRepository.save(newUser); // 保存到数据库
                return Optional.of(newUser);
            } else {
                // 用户已注册，直接返回该用户
                return existingUser;
            }
        } else {
            throw new IOException("Invalid ID token.");
        }
    }

    public ResponseEntity<String> registerUser(User user) {
        if (accountRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("該電子郵件已被使用!");
        }

        try {
            user.setId(generateNumericId());
            user.setRole("USER");
            user.setLoginAttempts(0); // 初始化登錄次數
            user.setAccountLocked(false); // 初始化帳戶鎖定狀態
            user.setCreatedAt(LocalDateTime.now()); // 設置創建時間
            user.setEmail(user.getEmail());
            user.setPassword(user.getPassword()); // 加密密码
            user.setDateOfBirth(user.getDateOfBirth());
            user.setHeight(user.getHeight());
            user.setWeight(user.getWeight());
            user.setGender(user.getGender());
            user.setPhoneNumber(user.getPhoneNumber());
            accountRepository.save(user);
            return ResponseEntity.ok("註冊成功! 請檢查您的電子郵件以完成註冊。");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("註冊失敗，請稍後重試。");
        }
    }

    public ResponseEntity<Map<String, String>> login(User user) {
        if (checkAccountLocked(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("message", "帳戶已被鎖定，請聯繫管理員"));
        }

        Optional<User> optionalUser = accountRepository.findByUsername(user.getUsername());
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            if (existingUser.getPassword().equals(user.getPassword())) {
                existingUser.setLoginAttempts(0); // 重置登錄嘗試次數
                accountRepository.save(existingUser);
                return ResponseEntity.ok(createLoginResponse(existingUser));
            } else {
                int attempts = existingUser.getLoginAttempts() + 1;
                existingUser.setLoginAttempts(attempts);
                if (attempts >= MAX_LOGIN_ATTEMPTS) {
                    existingUser.setAccountLocked(true);
                    accountRepository.save(existingUser);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Collections.singletonMap("message", "帳戶已被鎖定，請聯繫管理員"));
                } else {
                    accountRepository.save(existingUser);
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Collections.singletonMap("message", "密碼錯誤 " + attempts + " 次"));
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonMap("message", "用戶名或密碼錯誤"));
    }

    public boolean checkAccountLocked(String username) {
        return accountRepository.findByUsername(username)
                .map(User::getAccountLocked)
                .orElse(false);
    }

    public String generateNumericId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    // 移除成員變量中的 user，並在需要時手動創建
    private String jwtToken;

    private Map<String, String> createLoginResponse(User user) {
        User userdetail = accountRepository.findById(user.getId())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        CustomUserDetails userDetails = new CustomUserDetails(userdetail);
        Map<String, String> responseBody = new HashMap<>();
        jwtToken = jwtService.generateToken(user.getId(), userDetails);
        responseBody.put("token", jwtToken);
        return responseBody;
    }
}

