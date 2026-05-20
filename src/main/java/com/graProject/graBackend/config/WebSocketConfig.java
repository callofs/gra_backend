package com.graProject.graBackend.config;

import com.graProject.graBackend.websocket.PrivateMessageHandshakeInterceptor;
import com.graProject.graBackend.websocket.PrivateMessageWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 配置类。
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    /**
     * 私聊 WebSocket 处理器。
     */
    private final PrivateMessageWebSocketHandler privateMessageWebSocketHandler;

    /**
     * 私聊握手鉴权拦截器。
     */
    private final PrivateMessageHandshakeInterceptor privateMessageHandshakeInterceptor;

    /**
     * 构造方法。
     *
     * @param privateMessageWebSocketHandler     私聊 WebSocket 处理器
     * @param privateMessageHandshakeInterceptor 私聊握手鉴权拦截器
     */
    public WebSocketConfig(PrivateMessageWebSocketHandler privateMessageWebSocketHandler,
            PrivateMessageHandshakeInterceptor privateMessageHandshakeInterceptor) {
        this.privateMessageWebSocketHandler = privateMessageWebSocketHandler;
        this.privateMessageHandshakeInterceptor = privateMessageHandshakeInterceptor;
    }

    /**
     * 注册私聊 WebSocket 端点。
     *
     * @param registry WebSocket 注册器
     */
    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        registry.addHandler(privateMessageWebSocketHandler, "/ws/privateMessage")
                .addInterceptors(privateMessageHandshakeInterceptor)
                .setAllowedOriginPatterns("*");
    }
}
