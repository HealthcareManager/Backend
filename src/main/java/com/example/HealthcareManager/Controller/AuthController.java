package com.example.HealthcareManager.Controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.HealthcareManager.DTO.UserResponse;
import com.example.HealthcareManager.Model.PasswordResetToken;
import com.example.HealthcareManager.Model.User;
import com.example.HealthcareManager.Repository.AccountRepository;
import com.example.HealthcareManager.Security.JwtService;
import com.example.HealthcareManager.Service.AccountService;
import com.example.HealthcareManager.Service.EmailService;
import com.example.HealthcareManager.Service.PasswordResetTokenService;

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
    JwtService jwtService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        return accountService.registerUser(user);
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> tokenData) {
        String idToken = tokenData.get("idToken");
        System.out.println("idToken is " + idToken);
        try {
            Optional<UserResponse> userResponseOpt = accountService.verifyGoogleToken(idToken);
            if (userResponseOpt.isPresent()) {
                UserResponse userResponse = userResponseOpt.get();
                User userInfo = userResponse.getUser(); // 獲取 User 對象
                String jwtToken = userResponse.getJwtToken(); // 獲取 JWT token

                System.out.println("ResponseEntity to app... ID：" + userInfo.getId() +
                        " Username： " + userInfo.getUsername() +
                        " Email： " + userInfo.getEmail());

                UserResponse userResponseList = new UserResponse(userInfo.getId(), userInfo.getUsername(), userInfo.getEmail(), userInfo.getImagelink(), userInfo.getRole(), jwtToken);
                System.out.println("userResponseList role is " + userInfo.getRole());
                return ResponseEntity.ok(userResponseList);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Google token or user not found.");
            }
        } catch (GeneralSecurityException | IOException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token: " + e.getMessage());
        }
    }

    @PostMapping("/facebook-login")
    public ResponseEntity<?> facebookLogin(@RequestBody String accessToken) {
        System.out.println("--------------- accessToken at fblogin is: " + accessToken);
        try {
            Optional<UserResponse> userResponseOpt = accountService.verifyFacebookToken(accessToken);
            if (userResponseOpt.isPresent()) {
                UserResponse userResponse = userResponseOpt.get();
                User userInfo = userResponse.getUser(); // 獲取 User 對象
                String jwtToken = userResponse.getJwtToken(); // 獲取 JWT token

                System.out.println("ResponseEntity to app... ID：" + userInfo.getId() +
                        " Username： " + userInfo.getUsername() +
                        " Email： " + userInfo.getEmail());

                UserResponse userResponseList = new UserResponse(userInfo.getId(), userInfo.getUsername(), userInfo.getEmail(), userInfo.getImagelink(), userInfo.getRole(), jwtToken);

            return ResponseEntity.ok(userResponseList);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Facebook token or user not found.");
            }
        } catch (GeneralSecurityException | IOException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token: " + e.getMessage());
        }
        
    }

    @PostMapping("/line-callback")
    public ResponseEntity<UserResponse> lineCallback(@RequestBody Map<String, String> requestBody) {
        String code = requestBody.get("code"); // 从请求体中获取 "code"

        // 使用认证码交换访问令牌并获取用户信息
        Optional<UserResponse> userInfoResponse = accountService.fetchUserInfoWithAccessToken(code);
        if (userInfoResponse.isPresent()) {
            UserResponse userResponse = userInfoResponse.get();
            User user = userResponse.getUser(); // 获取 User 对象
            String jwtToken = userResponse.getJwtToken(); // 获取 JWT token

            // 创建返回的响应体

            UserResponse userResponseList = new UserResponse(user.getId(), user.getUsername(), user.getImagelink(), jwtToken);

            return ResponseEntity.ok(userResponseList);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    } 

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        System.out.println("user is " + user);
        return accountService.login(user);
    }

    @PostMapping("/validate-token")
    public ResponseEntity<Map<String, String>> getProtectedData(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "無效的身份驗證");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
        }

        String userId;
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            userId = userDetails.getUsername(); // 获取用户 ID
            System.out.println(userId);
        } else if (authentication.getPrincipal() instanceof String) {
            userId = (String) authentication.getPrincipal(); // 直接获取用户名
        } else {
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "無法識別身份驗證對象");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }

        Optional<User> optionalUser = accountRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("username", user.getUsername());
            responseBody.put("id", user.getId());
            responseBody.put("userImage", user.getImagelink());
            responseBody.put("email", user.getEmail());
            responseBody.put("password", user.getPassword());
            responseBody.put("gender", user.getGender());
            responseBody.put("height", user.getHeight() != null ? user.getHeight().toString() : "");
            responseBody.put("weight", user.getWeight() != null ? user.getWeight().toString() : "");
            responseBody.put("dateOfBirth", user.getDateOfBirth().toString());
            responseBody.put("role", user.getRole());
            return ResponseEntity.ok(responseBody);
        } else {
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "伺服器錯誤：用户不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
        }
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody User vo) {
        Optional<User> existingVo = accountRepository.findByEmail(vo.getEmail());
        System.out.println("input email is " + vo.getEmail());
        System.out.println("findByEmail user is " + existingVo);
        if (!existingVo.isPresent()) {
            return ResponseEntity.badRequest().body("電子郵件不存在");
        }

        // 创建密码重置令牌并获取生成的令牌
        String token = passwordResetTokenService.createPasswordResetTokenForUser(existingVo.get());

        // 调用 emailService 的方法来发送邮件
        emailService.sendResetPasswordEmail(existingVo.get(), token);

        return ResponseEntity.ok("信件寄送成功！請確認您的電子郵件信箱");
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword) {
        System.out.println("recive token is " + token + " newPassword is " +newPassword);
    	PasswordResetToken resetToken = passwordResetTokenService.validatePasswordResetToken(token);
        
        if (resetToken == null) {
            return ResponseEntity.badRequest().body("失效或過期的憑證");
        }
        
        accountService.changePassword(resetToken.getVo(), newPassword);
        passwordResetTokenService.deleteToken(resetToken);

        return ResponseEntity.ok("密碼重設成功！");
    }

}
