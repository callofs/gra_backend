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
 * 专家信息表
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_expert_info")
public class ExpertInfoDO implements Serializable {
    /**
     * 专家信息ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 对应用户ID，逻辑关联t_user.id
     */

    @TableField("user_id")
    private Long userId;
    /**
     * 专家真实姓名
     */

    @TableField("real_name")
    private String realName;
    /**
     * 专业资质
     */

    @TableField("qualification")
    private String qualification;
    /**
     * 工作单位
     */

    @TableField("work_unit")
    private String workUnit;
    /**
     * 擅长领域
     */

    @TableField("specialty")
    private String specialty;
    /**
     * 专家简介
     */

    @TableField("profile")
    private String profile;
    /**
     * 资质证书URL
     */

    @TableField("certificate_url")
    private String certificateUrl;
    /**
     * 审核状态 0=待审核 1=审核通过 2=审核驳回
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
