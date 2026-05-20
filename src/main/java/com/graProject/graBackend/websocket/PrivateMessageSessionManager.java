package com.graProject.graBackend.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 私聊 WebSocket 在线会话管理器。
 */
@Component
public class PrivateMessageSessionManager {

    /**
     * 在线会话映射。
     */
    private final Map<Long, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    /**
     * 注册用户会话。
     *
     * @param userId 用户 ID
     * @param session WebSocket 会话
     */
    public void register(Long userId, WebSocketSession session) {
        if (userId != null && session != null) {
            sessionMap.put(userId, session);
        }
    }

    /**
     * 移除用户会话。
     *
     * @param userId 用户 ID
     */
    public void remove(Long userId) {
        if (userId != null) {
            sessionMap.remove(userId);
        }
    }

    /**
     * 获取用户会话。
     *
     * @param userId 用户 ID
     * @return WebSocket 会话
     */
    public WebSocketSession getSession(Long userId) {
        return userId == null ? null : sessionMap.get(userId);
    }
}
