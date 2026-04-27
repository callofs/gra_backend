package com.graProject.graBackend.dto;

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
public class ExpertInfoDTO implements Serializable {
    /**
     * 专家信息ID
     */

    private Long id;
    /**
     * 对应用户ID，逻辑关联t_user.id
     */

    private Long userId;
    /**
     * 专家真实姓名
     */

    private String realName;
    /**
     * 专业资质
     */

    private String qualification;
    /**
     * 工作单位
     */

    private String workUnit;
    /**
     * 擅长领域
     */

    private String specialty;
    /**
     * 专家简介
     */

    private String profile;
    /**
     * 资质证书URL
     */

    private String certificateUrl;
    /**
     * 审核状态 0=待审核 1=审核通过 2=审核驳回
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
