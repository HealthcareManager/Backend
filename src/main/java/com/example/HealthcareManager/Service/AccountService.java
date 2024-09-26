package com.example.HealthcareManager.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.HealthcareManager.Model.User;
import com.example.HealthcareManager.Repository.AccountRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import io.jsonwebtoken.io.IOException;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;


@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    
    //private EmailService emailService;
    
//    public User getAccountById(Long userId) {
//        return accountRepository.findById(userId)
//            .orElseThrow(() -> new RuntimeException("User not found"));
//    }
    
    public Optional<User> findById(Long id) {
        return accountRepository.findById(id);
    }
    
    // 檢查用戶名是否已存在
    public boolean checkId(String username) {
        return accountRepository.findByUsername(username).isPresent();
    }

    // 檢查電子郵件是否已存在
    public boolean checkEmail(String email) {
        return accountRepository.findByEmail(email).isPresent();
    }
    
    // 檢查帳戶是否鎖定
    public boolean checkAccountLocked(String username) {
        return accountRepository.findByUsername(username)
                                .map(User::getAccountLocked)
                                .orElse(false);
    }
    
    public String generateNumericId() {
        UUID uuid = UUID.randomUUID();
        BigInteger bigInt = new BigInteger(uuid.toString().replace("-", ""), 16); // 从 UUID 字符串生成 BigInteger
        return bigInt.toString(); // 将其转换为数字字符串
    }
    
    // 檢查使用者輸入密碼是否正確，錯誤則計數，滿三次鎖定帳戶
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final String CLIENT_ID = "709151275791-j69ulvv0dlajor84m9v1lfb62m2gbur0.apps.googleusercontent.com"; // 替换为您的 Google 客户端 ID
    
    public Optional<User> verifyGoogleToken(String idTokenString) throws GeneralSecurityException, IOException, java.io.IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String userId = payload.getSubject();	// 用户的唯一 ID
            String name = (String) payload.get("name");
            String email = payload.getEmail();
            System.out.println("userId, name, email is " + userId + name + email);
            
         // 检查用户是否已注册
            Optional<User> existingUser = accountRepository.findByEmail(email); // 假设你有一个方法查找用户
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

    public ResponseEntity<String> checkUserPassword(String username, String inputPassword) {
        Optional<User> optionalUser = accountRepository.findByUsername(username);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user.getPassword().equals(inputPassword)) {
                user.setLoginAttempts(0); // 重置登錄嘗試次數
                accountRepository.save(user);
                
                return ResponseEntity.ok("登入成功");
            } else {
                int attempts = user.getLoginAttempts() + 1;
                user.setLoginAttempts(attempts);

                if (attempts >= MAX_LOGIN_ATTEMPTS) {
                	user.setAccountLocked(true);
                    accountRepository.save(user);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("帳戶已被鎖定，請聯繫管理員");
                } else {
                    accountRepository.save(user);
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("密碼錯誤 " + attempts + " 次");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用戶名或密碼錯誤");
    }
    
    // 插入新用戶資料
    public boolean insertUser(User user) {
        try {
            //String token = setVerificationToken(user);
            //user.setCreatedDate(LocalDateTime.now()); //設置註冊日期
            // 發送郵件
            //emailService.sendVerificationEmail(vo, token);
        	String uniqueId = generateNumericId();
            user.setId(uniqueId); 
            accountRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public ResponseEntity<String> registerUser(User user) {
        // 檢查用戶名是否已存在
//        if (accountRepository.findByUsername(user.getUsername()).isPresent()) {
//            return ResponseEntity.badRequest().body("該用戶名已被使用!");
//        }

        // 檢查電子郵件是否已存在
        if (accountRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("該電子信箱已被使用!");
        }

        // 插入新用戶資料
        try {       	
           if(insertUser(user)) {        	   
        	   return ResponseEntity.ok("註冊成功! 請檢查您的電子郵件以完成註冊。 即將回登入頁面...");
           }
        } catch (Exception e) {       	
            return ResponseEntity.status(500).body("註冊失敗，請稍後重試。");
        }     
		return null;
    }
    
//    public void changePassword(User user, String newPassword) {
//        // 通过用户名查找用户
//        User user = accountRepository.findByUsername(user.getUsername())
//                                             .orElseThrow(() -> new RuntimeException("用户不存在"));
//        // 更新密码
//        user.setPassword(newPassword);
//        // 保存更新后的用户信息
//        accountRepository.save(user);
//    }

    // 根據用戶名查找帳戶
    public Optional<User> findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    // 根據電子郵件查找帳戶
    public Optional<User> findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    // 根據驗證 token 查找帳戶
    public Optional<User> findByVerificationToken(String verificationToken) {
        return accountRepository.findByVerificationToken(verificationToken);
    }
    
    //生成隨機驗證帳戶的token
    private String generateVerificationToken() {
        // 實現 token 生成邏輯，這裡只是一個示例
        return java.util.UUID.randomUUID().toString();
    }
    
    //設置生成的token至資料庫
//    public String setVerificationToken(User user) {
//        String token = generateVerificationToken(); // 自定義方法生成驗證 token
//        user.setVerificationToken(token);
//        user.setTokenExpiration(LocalDateTime.now().plusHours(24));  // 設置24小時過期時間
//        accountRepository.save(user);  // 保存帳戶
//        
//        return token;
//    }
    
    // 驗證帳戶(Email)
//    public boolean verifyAccount(String token) {
//        Optional<User> accountOpt = findByVerificationToken(token);
//        if (accountOpt.isPresent()) {
//            User user = accountOpt.get();
//            user.setIsVerified(true);
//            user.setCreatedDate(LocalDateTime.now());
//            user.setVerificationToken(null);  // 驗證後移除 token
//            accountRepository.save(user);
//            return true;
//        }
//        return false;
//    }

//	public String findImageLinkByUsername(String username) {
//		
//		return accountRepository.findImageLinkByUsername(username);
//	}
    
}
