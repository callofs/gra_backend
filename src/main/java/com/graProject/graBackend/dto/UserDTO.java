package com.graProject.graBackend.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 用户表
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserDTO implements Serializable {
    /**
     * 用户唯一ID
     */

    private Long id;
    /**
     * 登录账号
     */

    private String username;
    /**
     * 登录密码（加密存储）
     */

    private String password;
    /**
     * 用户昵称
     */

    private String nickname;
    /**
     * 头像编码
     */

    private String avatar;
    /**
     * 手机号码
     */

    private String phone;
    /**
     * 电子邮箱
     */

    private String email;
    /**
     * 用户性别 0=未知 1=男 2=女
     */

    private Integer gender;
    /**
     * 所在地区
     */

    private String address;
    /**
     * 用户角色 1=普通家长 2=认证专家 3=平台管理员
     */

    private Integer role;
    /**
     * 信用分
     */

    private Integer creditScore;
    /**
     * 认证材料
     */

    private String certificationMaterials;
    /**
     * 账号状态 0=正常 1=禁用 2=待审核
     */

    private Integer status;
    /**
     * 创建时间
     */

    private LocalDateTime createTime;
    /**
     * 更新时间
     */

    private LocalDateTime updateTime;
    /**
     * 逻辑删除 0=未删除 1=已删除
     */

    private Integer isDelete;
}
