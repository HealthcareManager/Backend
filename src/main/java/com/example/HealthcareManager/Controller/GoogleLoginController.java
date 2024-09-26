package com.example.HealthcareManager.Controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.HealthcareManager.Model.User;
import com.example.HealthcareManager.Repository.UserRepository;
import com.example.HealthcareManager.Security.CustomUserDetails;
import com.example.HealthcareManager.Security.JwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

@RestController
public class GoogleLoginController {

    private final UserRepository userRepo;
    private final JwtService jwtService;

    public GoogleLoginController(UserRepository userRepo, JwtService jwtService) {
        this.userRepo = userRepo;
        this.jwtService = jwtService;
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody String idTokenString) {
        try {
            // 初始化 GoogleIdTokenVerifier，用來驗證 Google 登入的 ID Token
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList("879070403091-k6q58r307pifvmbquesf1ob164npra1c.apps.googleusercontent.com")) // 替換為你的 Google OAuth2 客戶端 ID
                    .build();

            // 驗證傳遞過來的 idTokenString
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail(); // 從 Google Payload 中取得用戶的 email
                String name = (String) payload.get("name"); // 從 Google Payload 中取得用戶的名稱

                // 檢查用戶是否已經存在於數據庫中
                Optional<User> optionalUser = userRepo.findByEmail(email);
                User user;
                if (optionalUser.isPresent()) {
                    user = optionalUser.get(); // 如果用戶存在，直接從數據庫中獲取
                } else {
                    // 如果用戶不存在，則創建一個新用戶
                    user = new User();
                    user.setEmail(email); // 設置用戶的 email
                    user.setUsername(name); // 設置用戶的名稱
                    user.setRole("ROLE_USER"); // 預設給用戶分配 "ROLE_USER" 角色

                    // 其他屬性如身高、體重、出生日期等可以保持為空，允許用戶後續填寫
                    userRepo.save(user); // 保存新用戶至數據庫
                }

                // 生成 JWT Token，並將用戶信息封裝到 Token 中
                CustomUserDetails userDetails = new CustomUserDetails(user);
                String jwtToken = jwtService.generateToken(user.getId(), userDetails);

                // 返回響應，包含 JWT Token 及用戶信息
                return ResponseEntity.ok(Map.of(
                        "token", jwtToken,
                        "email", email,
                        "roles", userDetails.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()),
                        "name", name));
            } else {
                // 如果 Token 驗證失敗，返回未授權的狀態
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google token");
            }

        } catch (GeneralSecurityException | IOException e) {
            // 如果發生驗證錯誤，返回伺服器內部錯誤
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Google login error: " + e.getMessage());
        }
    }
}
