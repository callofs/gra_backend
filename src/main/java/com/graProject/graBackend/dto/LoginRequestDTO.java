package com.graProject.graBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录请求对象。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO implements Serializable {

    /**
     * 登录账号。
     */
    private String username;

    /**
     * 登录密码。
     */
    private String password;
}
