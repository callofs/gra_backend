package com.graProject.graBackend.websocket;

import com.graProject.graBackend.common.utils.JwtTokenUtil;
import com.graProject.graBackend.dto.UserDTO;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

/**
 * 私聊 WebSocket 握手鉴权拦截器。
 */
@Component
public class PrivateMessageHandshakeInterceptor implements HandshakeInterceptor {

    /**
     * Bearer 前缀。
     */
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * JWT 工具类。
     */
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * JWT Cookie 名称。
     */
    private final String cookieName;

    /**
     * 构造方法。
     *
     * @param jwtTokenUtil JWT 工具类
     * @param cookieName   JWT Cookie 名称
     */
    public PrivateMessageHandshakeInterceptor(JwtTokenUtil jwtTokenUtil,
            @Value("${jwt.cookie.name:gra_token}") String cookieName) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.cookieName = cookieName;
    }

    /**
     * 在握手前校验 token 并写入登录用户信息。
     *
     * @param request    当前请求
     * @param response   当前响应
     * @param wsHandler  处理器
     * @param attributes 会话属性
     * @return 校验通过返回 true
     */
    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) {
        String token = resolveToken(request);
        if (token == null || !jwtTokenUtil.verifyToken(token)) {
            return false;
        }
        UserDTO userDTO = jwtTokenUtil.parseUserInfo(token);
        if (userDTO == null || userDTO.getId() == null) {
            return false;
        }
        attributes.put("loginUser", userDTO);
        return true;
    }

    /**
     * 握手完成后无额外处理。
     *
     * @param request   当前请求
     * @param response  当前响应
     * @param wsHandler 处理器
     * @param exception 异常
     */
    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler, @Nullable Exception exception) {
    }

    /**
     * 从握手请求中解析 token。
     *
     * @param request 当前请求
     * @return JWT 字符串
     */
    @Nullable
    private String resolveToken(@NonNull ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            Cookie[] cookies = servletRequest.getServletRequest().getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie != null && cookieName.equals(cookie.getName())
                            && StringUtils.hasText(cookie.getValue())) {
                        return cookie.getValue().trim();
                    }
                }
            }
        }
        HttpHeaders headers = request.getHeaders();
        String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith(BEARER_PREFIX)) {
            return authorization.substring(BEARER_PREFIX.length()).trim();
        }
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String token = servletRequest.getServletRequest().getParameter("token");
            if (StringUtils.hasText(token)) {
                return token.trim();
            }
        }
        URI uri = request.getURI();
        if (uri == null || !StringUtils.hasText(uri.getQuery())) {
            return null;
        }
        for (String pair : uri.getQuery().split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && "token".equals(kv[0]) && StringUtils.hasText(kv[1])) {
                return kv[1].trim();
            }
        }
        return null;
    }
}
