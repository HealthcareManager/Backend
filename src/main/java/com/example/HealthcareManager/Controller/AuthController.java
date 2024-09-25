package com.example.HealthcareManager.Controller;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.HealthcareManager.JwtUtil;
import com.example.HealthcareManager.Model.User;
import com.example.HealthcareManager.Repository.AccountRepository;
import com.example.HealthcareManager.Service.AccountService;


import groovy.lang.Lazy;
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
    
    @Autowired
    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
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

//                String token = JwtUtil.generateToken(
//                		accountRepository.findByUsername(vo.getUsername()).get().getId(),
//                        vo.getUsername(),
//                        accountRepository.findImageLinkByUsername(vo.getUsername()),
//                        vo.getPassword(),accountRepository.findByUsername(vo.getUsername()).get().getEmail());
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
    
    
}
