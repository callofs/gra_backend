package com.graProject.graBackend.common.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graProject.graBackend.common.result.HttpCode;
import com.graProject.graBackend.common.result.HttpResult;
import com.graProject.graBackend.common.utils.JwtTokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * JWT 认证拦截器。
 *
 * 用于从请求头中提取并校验 JWT，校验失败时直接返回统一格式的未登录响应。
 */
@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    /**
     * Bearer 令牌前缀。
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
     * JSON 序列化工具。
     */
    private final ObjectMapper objectMapper;

    /**
     * 构造方法。
     *
     * @param jwtTokenUtil JWT 工具类
     * @param cookieName   JWT Cookie 名称
     * @param objectMapper JSON 序列化工具
     */
    public JwtAuthInterceptor(JwtTokenUtil jwtTokenUtil,
            @Value("${jwt.cookie.name:gra_token}") String cookieName,
            ObjectMapper objectMapper) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.cookieName = cookieName;
        this.objectMapper = objectMapper;
    }

    /**
     * 在控制器执行前校验请求头中的 JWT。
     *
     * @param request  当前请求
     * @param response 当前响应
     * @param handler  处理器
     * @return 校验通过返回 true，否则返回 false
     * @throws Exception 序列化响应失败时抛出异常
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull Object handler) throws Exception {
        String token = resolveToken(request);
        if (!StringUtils.hasText(token) || !jwtTokenUtil.verifyToken(token)) {
            writeUnauthorizedResponse(response);
            return false;
        }
        request.setAttribute("loginUser", jwtTokenUtil.parseUserInfo(token));
        return true;
    }

    /**
     * 从请求中提取 JWT。
     *
     * 优先从 Cookie 中读取，若不存在再回退到 Authorization 请求头。
     *
     * @param request 当前请求
     * @return 解析得到的 token，不存在时返回 null
     */
    private String resolveToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie != null && cookieName.equals(cookie.getName()) && StringUtils.hasText(cookie.getValue())) {
                    return cookie.getValue().trim();
                }
            }
        }
        String authorization = request.getHeader("Authorization");
        if (!StringUtils.hasText(authorization) || !authorization.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return authorization.substring(BEARER_PREFIX.length()).trim();
    }

    /**
     * 输出未登录或登录过期响应。
     *
     * @param response 当前响应
     * @throws Exception JSON 序列化失败时抛出异常
     */
    private void writeUnauthorizedResponse(HttpServletResponse response) throws Exception {
        response.setStatus(HttpCode.UNAUTHORIZED.getCode());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        HttpResult<Object> result = HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
