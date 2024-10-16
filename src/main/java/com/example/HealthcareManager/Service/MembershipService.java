package com.example.HealthcareManager.Service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.HealthcareManager.MembershipWebSocketHandler;
import com.example.HealthcareManager.Model.User;
import com.example.HealthcareManager.Repository.UserRepository;

@Service
public class MembershipService {

    @Autowired
    private MembershipWebSocketHandler webSocketHandler;
    @Autowired
    private UserRepository userRepository;

    @Scheduled(cron = "*/15 * * * * ?") // 每24小时执行一次检查
    public void checkExpiredMemberships() {
        List<User> users = userRepository.findAll();
        LocalDate now = LocalDate.now();
        for (User user : users) {
        	if (user.getId().equals("115203853604921130467")) {
        		System.out.println("找到用户: " + user.getUsername());
                // 执行您需要的逻辑
        		if (user.getMembershipEndDate().isBefore(now) && "VIP".equals(user.getRole())) {
        		    //System.out.println("找到過期用户: " + user.getUsername());
        		    
        		    // 将角色更新为 USER
        		    user.setRole("USER");
        		    user.setMembershipEndDate(null);
        		    userRepository.save(user);

        		    // 重新获取用户状态（可选，确保持久化成功后状态一致）
        		    user = userRepository.findById(user.getId()).orElse(null);

        		    // 确保用户已更新角色为 USER
        		    if ("USER".equals(user.getRole())) {
        		        // 发送 WebSocket 消息
        		        webSocketHandler.sendMembershipUpdate(user);
        		        System.out.println("會員狀態已更新，發送 WebSocket 訊息");
        		    }
        		}

            }
        }
    }
}

