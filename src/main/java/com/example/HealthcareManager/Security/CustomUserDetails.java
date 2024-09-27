package com.example.HealthcareManager.Security;

import com.example.HealthcareManager.Model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L; // 設置 serialVersionUID

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 假設 user.getRole() 返回的角色用逗號分隔
        String rolesString = user.getRole();
        System.out.println(rolesString);
        String[] rolesArray = rolesString.split(","); // 根據逗號分隔角色

        // 將角色轉換為 GrantedAuthority 集合
        return Arrays.stream(rolesArray)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim())) // 去除多餘的空白
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
    	System.out.println("id at CUD is" + user.getId());
        return user.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
