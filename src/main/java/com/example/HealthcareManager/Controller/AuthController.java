package com.example.HealthcareManager.Controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.HealthcareManager.JwtUtil;
import com.example.HealthcareManager.Model.User;
import com.example.HealthcareManager.Repository.AccountRepository;
import com.example.HealthcareManager.Service.AccountService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    public AuthController(AccountService accountService) {
        this.accountService = accountService;
    }
    
    @GetMapping("/register")
    public String getRegistrationPage() {
        return "Registration API - Use POST method to register.";
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {

        ResponseEntity<String> response = accountService.registerUser(user);
        return response;
    }
    
    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> tokenData) {
    	System.out.println("Received ID Token: " + tokenData); 
    	String idToken = tokenData.get("idToken");
        try {
            Optional<User> user = accountService.verifyGoogleToken(idToken);
            // 在这里您可以将用户信息存储到数据库，生成 JWT 等操作
            System.out.println("user at google login is " + user);
            if (user.isPresent()) {
                User userInfo = user.get();
                // 创建响应体，您可以选择要返回的用户信息
                User userResponse = new User(userInfo.getId(), userInfo.getUsername(), userInfo.getEmail());

                return ResponseEntity.ok(userResponse);
            } else {
                return ResponseEntity.status(400).body("Invalid Google token or user not found.");
            }
        } catch (GeneralSecurityException | IOException e) {
            return ResponseEntity.status(401).body("Invalid token: " + e.getMessage());
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user, HttpServletRequest request,
            HttpServletResponse response) {
    	try {

            // 查詢用戶是否存在
            if (!accountService.checkId(user.getUsername())) {
                System.out.println("\033[0;31m" + "未知的使用者名稱：" + user.getUsername() + " 於 "
                        + new Date(System.currentTimeMillis()) + " 嘗試登入" + "\033[0m");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "使用者不存在"));
            }

            // 確認用戶是否處於鎖定狀態
            if (accountService.checkAccountLocked(user.getUsername())) {
            	System.out.println("用戶： " + user.getUsername() + " 帳戶已鎖定");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message", "帳戶已被鎖定，請聯繫管理員"));
            }

            // 调用 AccountService 的 checkUserPassword 方法
            ResponseEntity<String> checkUserPasswordResponse = accountService.checkUserPassword(user.getUsername(),
                    user.getPassword());
            if (checkUserPasswordResponse.getStatusCode() == HttpStatus.UNAUTHORIZED
                    || checkUserPasswordResponse.getStatusCode() == HttpStatus.FORBIDDEN) {
                // 将错误信息封装为 Map
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", checkUserPasswordResponse.getBody());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            System.out.println("\033[0;32m" + "使用者：" + user.getUsername() + " 於 " + new Date(System.currentTimeMillis())
                    + " 登入" + "\033[0m");

            // 如果验证成功，生成 JWT
            if (checkUserPasswordResponse.getStatusCode() == HttpStatus.OK) {

            	String token = JwtUtil.generateToken(
                		accountRepository.findByUsername(user.getUsername()).get().getId());
                System.out.println("id is(at login) " + accountRepository.findByUsername(user.getUsername()).get().getId());
                // 将 JWT 添加到响应头中
                response.setHeader("Authorization", "Bearer " + token);
                System.out.println("已生成token:" + token);
                // 返回 JSON 对象
                Map<String, String> responseBody = new HashMap<>();
                responseBody.put("token", token);

                return ResponseEntity.ok(responseBody);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "伺服器錯誤：" + e.getMessage()));
        }
        return null;
    }
    
    @PostMapping("/validate-token")
	public ResponseEntity<Map<String, String>> getProtectedData(@RequestHeader(value = "Authorization", required = false) String authHeader) {
	    
    	// 檢查 Authorization 頭是否存在
	    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	        Map<String, String> responseBody = new HashMap<>();
	        responseBody.put("message", "缺少或無效的 Authorization");
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
	    }
	    
	    try {
	        // 提取 token
	        String token = authHeader.replace("Bearer ", "").trim();
	        Long id = JwtUtil.extractId(token);
	        
	        // 檢查 token 和 username 的有效性
	        if (id == null || !JwtUtil.validateToken(token)) {
	            Map<String, String> responseBody = new HashMap<>();
	            responseBody.put("message", "無效的token");
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
	        }

	        // 生成保護的數據
	        Map<String, String> responseBody = new HashMap<>();
	        responseBody.put("username", accountRepository.findById(id).get().getUsername());
	        responseBody.put("id", String.valueOf(id));
	        responseBody.put("userImage", accountRepository.findById(id).get().getImagelink());
	        responseBody.put("password", accountRepository.findById(id).get().getPassword());
	        responseBody.put("email", accountRepository.findById(id).get().getEmail());
	        
	        return ResponseEntity.ok(responseBody);
	    } catch (Exception e) {
	        // 捕獲異常並返回錯誤訊息
	        Map<String, String> responseBody = new HashMap<>();
	        responseBody.put("message", "伺服器錯誤：" + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
	    }
	}
    
}
