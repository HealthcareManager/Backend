package com.example.HealthcareManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;

import com.example.HealthcareManager.Model.User;

@Component
public class MembershipWebSocketHandler extends TextWebSocketHandler {

	private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>(); // 确保线程安全

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
	    // 添加新的 WebSocket 会话到 sessions 列表
	    sessions.add(session);
	    System.out.println("目前 WebSocket 連線數量：" + sessions.size());
	}

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("WebSocket 连接关闭，剩余连接数：" + sessions.size());
        System.out.println("关闭原因：" + status.getReason());
    }

    public void sendMembershipUpdate(User user) {
    	System.out.println("目前 WebSocket 連線數量：" + sessions.size());
    	System.out.println("userId：" + user.getId() + "membershipStatus：" + user.getRole());
        String message = String.format("{\"userId\": \"%s\", \"membershipStatus\": \"%s\"}",
                                        user.getId(), user.getRole());
        for (WebSocketSession session : sessions) {
            try {
                System.out.println("傳送給用戶：" + user.getUsername() + "訊息：" + message);
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void sendTestMessageToAll() {
        String testMessage = "測試訊息，檢查 WebSocket 連接";
        System.out.println("目前 WebSocket 連線數量：" + sessions.size());  // 打印当前会话数
        for (WebSocketSession session : sessions) {
            try {
                System.out.println("發送測試訊息：" + testMessage);
                session.sendMessage(new TextMessage(testMessage));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

