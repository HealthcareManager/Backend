package com.example.HealthcareManager.Security;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService; // 使用自訂的 UserDetailsService

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 打印前端請求的 URL
        String requestUrl = request.getRequestURL().toString();
        logger.info("Incoming request URL: {}", requestUrl);

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userId;

        String requestURI = request.getRequestURI();
        System.out.println(requestURI);
        if (requestURI.equals("/HealthcareManager/api/auth/login") || requestURI.equals("/HealthcareManager/api/auth/register") || requestURI.equals("/HealthcareManager/api/auth/google-login") || requestURI.equals("/HealthcareManager/api/auth/facebook-login") || requestURI.equals("/HealthcareManager/api/auth/line-callback") || requestURI.equals("/HealthcareManager/membership-status") || requestURI.equals("/HealthcareManager/api/auth/forgot-password") || requestURI.equals("/HealthcareManager/api/auth/reset-password") || requestURI.startsWith("/HealthcareManager/images")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (authHeader == null || authHeader.trim().isEmpty()) {
                logger.warn("Authorization header is missing or empty");
                filterChain.doFilter(request, response);
                return;
            }

            // 檢查 Authorization header 是否以 'Bearer ' 開頭
            if (!authHeader.startsWith("Bearer ")) {
                logger.warn("Authorization header does not start with 'Bearer '");
                filterChain.doFilter(request, response);
                return;
            }

            // 獲取 JWT
            jwt = authHeader.substring(7);

            // 檢查 JWT 是否有效
            if (!jwtService.isTokenValid(jwt)) {
                logger.warn("JWT is not valid or not signed with the correct secret key");
                filterChain.doFilter(request, response);
                return;
            }

            // 從 JWT 中提取 userId（subject）
            userId = jwtService.extractId(jwt);

            if (userId == null) {
                logger.warn("Failed to extract userId from JWT token");
                filterChain.doFilter(request, response);
                return;
            }

            // 檢查 SecurityContext 中是否已經存在認證
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                logger.warn("Authentication is already set in the security context");
                filterChain.doFilter(request, response);
                return;
            }

            // 根據 userId 加載使用者資訊
            UserDetails userDetails = customUserDetailsService.loadUserById(userId);

            // 如果找不到該 userId，則不進行後續處理
            if (userDetails == null) {
                logger.error("UserDetailsService failed to load user by Id: {}", userId);
                filterChain.doFilter(request, response);
                return;
            }

            // 創建 UsernamePasswordAuthenticationToken 以設置安全上下文
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            // 設置附加細節
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 將認證信息設置到 SecurityContext 中
            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (UsernameNotFoundException e) {
            logger.error("User not found during authentication: {}", e.getMessage());
        } catch (JwtException e) {
            logger.error("Failed to parse or validate JWT token: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error occurred during JWT authentication", e); // 捕捉其他潛在異常
        }

        // 繼續進行其他過濾器
        filterChain.doFilter(request, response);
    }
}

