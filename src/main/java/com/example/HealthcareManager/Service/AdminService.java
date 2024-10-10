package com.example.HealthcareManager.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.HealthcareManager.Model.User;
import com.example.HealthcareManager.Repository.AccountRepository;
import com.example.HealthcareManager.Security.CustomUserDetails;
import com.example.HealthcareManager.Security.JwtService;

@Service
public class AdminService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    public Map<String, String> login(User userDao) {
        Map<String, String> userdata = new HashMap<>();
        

        User user = accountRepository.findByUsername(userDao.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String userId = user.getId(); 
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String jwtToken = jwtService.generateToken(userId, userDetails);

        // 獲取用戶角色信息，將其用逗號分隔成一個字串
        String roles = userDetails.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
        userdata.put("id", user.getId());
        userdata.put("token", jwtToken);
        userdata.put("username", user.getUsername());
        userdata.put("imagelink", user.getImagelink());
        userdata.put("roles", roles);  // 使用逗號分隔角色

        System.out.println(userdata);
        
        return userdata; 
    }

    public Map<String, String> updateUserRole(String userId, String role){

        Map<String, String> response = new HashMap<>();

        Optional<String> originRole = accountRepository.getRoleByUserId(userId);
        if (originRole.isEmpty()) {
            response.put("message", "User not found");
            return response;
        }

        User user = accountRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setRole(role);
        accountRepository.save(user);
        

        response.put("message", "successful update!");
        return response;
    }
}


