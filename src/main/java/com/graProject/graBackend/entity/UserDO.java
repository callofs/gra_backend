package com.graProject.graBackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
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
@Data
@TableName("t_user")
public class UserDO implements Serializable {
    /**
     * 用户唯一ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 登录账号
     */

    @TableField("username")
    private String username;
    /**
     * 登录密码（加密存储）
     */

    @TableField("password")
    private String password;
    /**
     * 用户昵称
     */

    @TableField("nickname")
    private String nickname;
    /**
     * 头像编码
     */

    @TableField("avatar")
    private String avatar;
    /**
     * 手机号码
     */

    @TableField("phone")
    private String phone;
    /**
     * 电子邮箱
     */

    @TableField("email")
    private String email;
    /**
     * 用户性别 0=未知 1=男 2=女
     */

    @TableField("gender")
    private Integer gender;
    /**
     * 所在地区
     */

    @TableField("address")
    private String address;
    /**
     * 用户角色 1=普通家长 2=认证专家 3=平台管理员
     */

    @TableField("role")
    private Integer role;
    /**
     * 信用分
     */

    @TableField("credit_score")
    private Integer creditScore;
    /**
     * 认证材料
     */

    @TableField("certification_materials")
    private String certificationMaterials;
    /**
     * 账号状态 0=正常 1=禁用 2=待审核
     */

    @TableField("status")
    private Integer status;
    /**
     * 创建时间
     */

    @TableField("create_time")
    private LocalDateTime createTime;
    /**
     * 更新时间
     */

    @TableField("update_time")
    private LocalDateTime updateTime;
    /**
     * 逻辑删除 0=未删除 1=已删除
     */

    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;
}
