package com.graProject.graBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户注册请求参数。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto implements Serializable {
    /**
     * 用户登录账号。
     */
    private String username;
    /**
     * 用户登录密码。
     */
    private String password;
    /**
     * 用户昵称。
     */
    private String nickname;

    /**
     * 用户手机号。
     */
    private String phone;

    /**
     * 用户角色 1=普通用户 2=专家 3=管理员。
     */
    private Integer role;

    /**
     * 用户邮箱。
     */
    private String email;
}
