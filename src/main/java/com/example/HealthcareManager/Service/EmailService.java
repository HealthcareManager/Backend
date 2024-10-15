package com.example.HealthcareManager.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.HealthcareManager.Model.User;
import com.example.HealthcareManager.Repository.AccountRepository;


@Service
public class EmailService {
    @Autowired
    private final JavaMailSender mailSender;

    @Autowired
    private AccountRepository accountRepository;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendResetPasswordEmail(User vo, String token) {
        String resetLink = "https://healthcaremanager.myvnc.com/reset-password?token=" + token;
        //String resetLink = "http://localhost:3000/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(vo.getEmail());
        message.setSubject("重設密碼請求");
        message.setText("請點擊以下連結重設您的密碼:\n" + resetLink);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    public void sendVerificationEmail(User vo, String token) {
        //String verificationUrl = "http://localhost:3000/verify-email?token=" + token;
        String verificationUrl = "http://niceblog.myvnc.com:81/verify-email?token=" + token;
        String subject = "請驗證您的電子郵件地址";
        String content = "親愛的 " + vo.getUsername() + "，\n\n" +
                "請點擊以下連結以驗證您的電子郵件地址：\n" + verificationUrl +
                "\n\n謝謝！";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(vo.getEmail());
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }

//    public String verifyEmail(String token) {
//        Optional<User> vo = accountRepository.findByVerificationToken(token);
//        if (!vo.isPresent()) {
//            return "無效的驗證連結";
//        }
//
//        User account = vo.get();
//        if (account.getTokenExpiration().isBefore(LocalDateTime.now())) {
//            return "驗證連結已過期";
//        }
//
//        account.setIsVerified(true);
//        account.setVerificationToken(null); // 清除 token
//        account.setTokenExpiration(null);
//        accountRepository.save(account);
//
//        return "您的帳號已成功驗證";
//    }
}
