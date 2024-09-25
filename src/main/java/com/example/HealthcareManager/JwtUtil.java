package com.example.HealthcareManager;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

public class JwtUtil {
    // 生成一個安全密鑰
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 生成JWT token
    public static String generateToken(Long id) {
        return Jwts.builder()
                .setSubject(id.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10小时过期
                .signWith(SECRET_KEY) // 修正簽名方式
                .compact();
    }

    // 提取Claims
    public static Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY) // 修正了密鑰設置方法
                .build() // 使用builder方法
                .parseClaimsJws(token)
                .getBody();
    }

    // 提取ID，將id轉換為Long
    public static Long extractId(String token) {
        String subject = extractClaims(token).getSubject();
        return Long.parseLong(subject);  // 將 subject 轉換為 Long
    }

    // 提取用户名
    public static String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // 提取使用者圖檔位置
    public static String extractImageLink(String token) {
        return (String) extractClaims(token).get("imagelink");
    }

    // 提取使用者密碼
    public static String extractPassword(String token) {
        return (String) extractClaims(token).get("password");
    }
    
    public static String extractEmail(String token) {
        return (String) extractClaims(token).get("email");
    }

    // 檢查token是否過期
    public static boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // 驗證token是否有效
    public static boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    
}
