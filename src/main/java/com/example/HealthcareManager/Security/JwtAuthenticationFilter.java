package com.example.HealthcareManager.Security;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userId;

        String requestURI = request.getRequestURI();
        System.out.println(requestURI);
        if (requestURI.equals("/api/auth/login") || requestURI.equals("/api/auth/register") || requestURI.equals("/api/auth/google-login") || requestURI.equals("/api/auth/facebook-login") || requestURI.equals("/api/auth/line-callback") || requestURI.equals("/api/admin/admin-login")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (authHeader == null || authHeader.trim().isEmpty()) {
                logger.warn("Authorization header is missing or empty");
                createErrorResponse(response, HttpStatus.UNAUTHORIZED, "缺少 Authorization 頭");
                return;
            }

            if (!authHeader.startsWith("Bearer ")) {
                logger.warn("Authorization header does not start with 'Bearer '");
                createErrorResponse(response, HttpStatus.UNAUTHORIZED, "Authorization 頭必須以 'Bearer ' 開頭");
                return;
            }

            jwt = authHeader.substring(7);
            userId = jwtService.extractId(jwt);

            if (userId == null) {
                logger.warn("Failed to extract username from JWT token");
                createErrorResponse(response, HttpStatus.UNAUTHORIZED, "無效的 JWT：無法解析Id");
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                logger.warn("Authentication is already set in the security context");
                filterChain.doFilter(request, response);
                return;
            }

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userId);
            if (userDetails == null) {
                logger.error("UserDetailsService failed to load user by Id: {}", userId);
                createErrorResponse(response, HttpStatus.UNAUTHORIZED, "無效的 JWT：用戶不存在");
                return;
            }

            
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (Exception e) {
            logger.error("JWT authentication filter failed: {}", e.getMessage());
            createErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "伺服器錯誤：" + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void createErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write("{\"message\": \"伺服器錯誤：" + message + "\"}");
    }
}


