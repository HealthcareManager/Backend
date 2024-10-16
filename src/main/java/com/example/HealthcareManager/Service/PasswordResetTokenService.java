package com.example.HealthcareManager.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.HealthcareManager.Model.PasswordResetToken;
import com.example.HealthcareManager.Model.User;
import com.example.HealthcareManager.Repository.AccountRepository;
import com.example.HealthcareManager.Repository.PasswordResetTokenRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetTokenService {

	@Autowired 
	PasswordResetTokenRepository tokenRepository;
    
    @Autowired 
    AccountRepository accountRepository;

    public PasswordResetTokenService(PasswordResetTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String createPasswordResetTokenForUser(User vo) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1); // 令牌有效期 1 小時
        // 检查 `AccountVo` 是否已经被保存，如果没有，则保存
        if (vo.getId() == null) {  // 假设 `id` 是 `AccountVo` 的主键字段
            vo = accountRepository.save(vo);
        }
        PasswordResetToken resetToken = new PasswordResetToken(token, vo, expiryDate);
        tokenRepository.save(resetToken);
        
        return token;
    }

    public PasswordResetToken validatePasswordResetToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);

        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {

            return null; // 令牌無效或過期
        }

        return resetToken;
    }
    
    public void deleteToken(PasswordResetToken token) {
    	tokenRepository.delete(token); // 假设你有一个 repository 来管理 token
    }
}
