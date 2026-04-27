package com.graProject.graBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录响应对象。
 *
 * 在 HttpOnly Cookie 方案下，JWT 由后端写入 Cookie，响应体主要返回用户信息和过期时间。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO implements Serializable {

    /**
     * JWT 令牌。
     *
     * 该字段主要用于后端写入 Cookie，正常情况下不会直接返回给前端。
     */
    private String token;

    /**
     * 令牌类型。
     *
     * 该字段主要用于后端内部处理，正常情况下不会直接返回给前端。
     */
    private String tokenType;

    /**
     * 过期时间，单位为秒。
     */
    private Long expiresIn;

    /**
     * 当前登录用户信息。
     */
    private UserDTO userInfo;
}
