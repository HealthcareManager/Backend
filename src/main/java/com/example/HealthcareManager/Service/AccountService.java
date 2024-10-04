package com.example.HealthcareManager.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.example.HealthcareManager.Model.User;
import com.example.HealthcareManager.Repository.AccountRepository;
import com.example.HealthcareManager.Security.CustomUserDetails;
import com.example.HealthcareManager.Security.JwtService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private static final String Google_CLIENT_ID = "709151275791-j69ulvv0dlajor84m9v1lfb62m2gbur0.apps.googleusercontent.com"; // 替换为您的 Google
    private static final String LINE_TOKEN_URL = "https://api.line.me/oauth2/v2.1/token";
    private static final String LINE_USER_INFO_URL = "https://api.line.me/v2/profile";
    private static final String LINE_Channel_ID = "2006371057"; // 从 LINE Developers 获取
    private static final String LINE_CLIENT_SECRET = "b0fc2958b7a310628b4d3bcb6ccdda00"; // 从 LINE Developers 获取
    private static final String LINE_REDIRECT_URI = "http://192.168.50.38:8080/HealthcareManager/api/auth/line-callback";
    
    // 移除 @Autowired 和構造函數中的 User 注入
    public AccountService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    public Optional<User> getLineAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        // 构建请求体
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", LINE_REDIRECT_URI);
        body.add("client_id", LINE_Channel_ID);
        body.add("client_secret", LINE_CLIENT_SECRET);
        System.out.println("body send to line is " + body);

        // 发送 POST 请求以获取访问令牌
        ResponseEntity<Map> response = restTemplate.postForEntity(LINE_TOKEN_URL, body, Map.class);
        Map<String, Object> responseBody = response.getBody();
        System.out.println("responseBody is " + responseBody);

        // 返回访问令牌
        String accessToken = responseBody != null ? (String) responseBody.get("access_token") : null;

        if (accessToken != null) {
            // 直接使用访问令牌获取用户信息
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // 发送 GET 请求以获取用户信息
            ResponseEntity<Map> userInfoResponse = restTemplate.exchange(LINE_USER_INFO_URL, HttpMethod.GET, entity, Map.class);
            Map<String, Object> userInfo = userInfoResponse.getBody();
            System.out.println("userInfo is " + userInfo);

            // 检查用户信息是否存在
            if (userInfo != null) {
                String userId = (String) userInfo.get("userId"); // 假设用户信息中有 userId 字段
                Optional<User> existingUser = accountRepository.findById(userId);
                System.out.println("displayName is " + existingUser);
                if (!existingUser.isPresent()) {
                    // 如果用户不存在，则新增用户
                    User newUser = new User();
                    newUser.setId(userId);
                    //newUser.setEmail((String) userInfo.get("email")); // 假设用户信息中有 email 字段
                    newUser.setUsername((String) userInfo.get("displayName")); // 假设用户信息中有 name 字段
                    newUser.setImagelink(((String) userInfo.get("pictureUrl")));;
                    // 设置其他必要的字段

                    accountRepository.save(newUser);
                    return Optional.of(newUser); // 返回新创建的用户
                }

                return existingUser; // 如果用户存在，返回现有用户
            }
        }

        return Optional.empty(); // 如果访问令牌为空，返回空的 Optional
    }

    public Optional<User> verifyGoogleToken(String idTokenString)
            throws GeneralSecurityException, IOException, java.io.IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(Google_CLIENT_ID))
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
    
    public Optional<User> verifyFacebookToken(String idTokenString)
            throws GeneralSecurityException, IOException, java.io.IOException {
        String url = String.format("https://graph.facebook.com/me?access_token=%s&fields=id,name,email", idTokenString.replace("\"", ""));
        System.out.println("url is " + url);

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        System.out.println("Response from Facebook: " + response);

        // 将返回结果转换为 Map，以便获取特定字段
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> userMap = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});

        // 提取字段
        String facebookId = (String) userMap.get("id");
        String name = (String) userMap.get("name");
        String email = (String) userMap.get("email");

        Optional<User> existingUser = accountRepository.findById(facebookId);
        if (!existingUser.isPresent()) {
            // 创建新用户
            User newUser = new User(facebookId, name, email); // 使用 username
            accountRepository.save(newUser);
            return Optional.of(newUser);
        } else {
            // 更新用户资料（如果需要）
            existingUser.get().setUsername(name);
            existingUser.get().setEmail(email);
            accountRepository.save(existingUser.get());
            return existingUser;
        }
    }

    private final PasswordEncoder passwordEncoder;
    public ResponseEntity<String> registerUser(User user) {

        if (accountRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("使用者名稱已被使用!");
        } else if(accountRepository.findByEmail(user.getEmail()).isPresent()){
            return ResponseEntity.badRequest().body("Email名稱已被使用!");
        }

        try {
            user.setId(generateNumericId());
            user.setRole("USER");
            user.setLoginAttempts(0); // 初始化登錄次數
            user.setAccountLocked(false); // 初始化帳戶鎖定狀態
            user.setCreatedAt(LocalDateTime.now()); // 設置創建時間
            user.setEmail(user.getEmail());
            user.setPassword(user.getPassword()); 
            user.setPassword(passwordEncoder.encode(user.getPassword())); // 加密密码
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

    @Autowired
    private AuthenticationManager authenticationManager;

    public ResponseEntity<Map<String, String>> login(User user) {

         try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    user.getPassword()
                )
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("message", "Failed Login")); // 返回錯誤信息和錯誤次數
        }
        if (checkAccountLocked(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("message", "帳戶已被鎖定，請聯繫管理員"));
        }
        Optional<User> optionalUser = accountRepository.findByUsername(user.getUsername());
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            if (passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
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
    
    private String decodeUnicode(String unicode) {
        StringBuilder string = new StringBuilder();
        String[] unicodeArr = unicode.split("\\\\u");
        for (String s : unicodeArr) {
            if (s.length() > 0) {
                int codePoint = Integer.parseInt(s, 16);
                string.append((char) codePoint);
            }
        }
        return string.toString();
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
        System.out.println("token create ：" + responseBody);
        return responseBody;
    }
}