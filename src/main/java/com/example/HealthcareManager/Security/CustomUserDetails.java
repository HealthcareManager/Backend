package com.example.HealthcareManager.Security;

import com.example.HealthcareManager.Model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final User user;

    // 構造函數應接受 com.example.HealthcareManager.Model.User 類型
    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 將用戶的角色轉換為 GrantedAuthority
        String role = user.getRole();
        return List.of(new SimpleGrantedAuthority("ROLE_" + role)); // 如果只有一個角色
    }

    @Override
    public String getPassword() {
        // 返回用戶的密碼，這裡可以選擇返回 null，因為使用的是第三方登入
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // 返回用戶名（這裡使用 email 作為用戶名）
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        // 假設帳號不會過期
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 根據用戶的 `accountLocked` 屬性來決定帳號是否被鎖定
        return !user.getAccountLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 憑證是否未過期
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 用戶是否啟用
        return user.isEnabled();
    }

    // 自定義方法來獲取用戶的其他信息
    public User getUser() {
        return user;
    }
}
