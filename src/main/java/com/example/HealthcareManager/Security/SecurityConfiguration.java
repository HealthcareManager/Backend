package com.example.HealthcareManager.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService customUserDetailsService;

    @Autowired
    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter, 
                                 UserDetailsService customUserDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customUserDetailsService = customUserDetailsService;
    }

    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //     http
    //         .cors(cors -> cors
    //             .configurationSource(request -> {
    //                 CorsConfiguration config = new CorsConfiguration();
    //                 config.applyPermitDefaultValues();
    //                 // 根據需求自定義CORS配置
    //                 return config;
    //             })
    //         )
    //         .csrf(csrf -> csrf.disable()) // 禁用 CSRF，根據需要決定是否啟用
    //         .authorizeHttpRequests(authz -> authz
    //             .requestMatchers("/img/**", "/news/**").permitAll()
    //             .requestMatchers("/api/manager/**").hasRole("MANAGER")
    //             .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "MANAGER")
    //             .requestMatchers("/api/user/**").hasRole("USER")
    //             .anyRequest().authenticated()
    //         )
    //         .oauth2Login(oauth2Login -> oauth2Login
    //             .userInfoEndpoint(userInfo -> userInfo
    //                 .userService((OAuth2UserService<OAuth2UserRequest, OAuth2User>) customUserDetailsService)
    //             )
    //             .successHandler((request, response, authentication) -> {
    //                 // OAuth2 認證成功處理
    //             })
    //         )
    //         .sessionManagement(session -> session
    //             .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 使用無狀態的 Session 管理
    //         )
    //         .authenticationProvider(authenticationProvider()) // 設置自定義的 AuthenticationProvider
    //         .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // 添加 JWT 過濾器

    //     return http.build();
    // }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
