package com.example.HealthcareManager.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.HealthcareManager.Model.User;
import com.example.HealthcareManager.Repository.AccountRepository;

import java.time.LocalDateTime;
import java.util.Optional;


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
    
    // 檢查使用者輸入密碼是否正確，錯誤則計數，滿三次鎖定帳戶
    private static final int MAX_LOGIN_ATTEMPTS = 5;

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
            accountRepository.save(user);
            return true;
        } catch (Exception e) {
        	
            return false;
        }
    }
    
    public ResponseEntity<String> registerUser(User user) {
        // 檢查用戶名是否已存在
        if (accountRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("該用戶名已被使用!");
        }

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
