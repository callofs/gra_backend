package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 咨询预约表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConsultReservationDTO implements Serializable {
    /**
     * 预约ID
     */

    private Long id;
    /**
     * 预约用户ID，逻辑关联t_user.id
     */

    private Long userId;
    /**
     * 被预约专家ID，逻辑关联t_user.id
     */

    private Long expertId;
    /**
     * 关联排班ID，逻辑关联t_expert_schedule.id
     */

    private Long scheduleId;
    /**
     * 咨询类型 1=图文 2=语音 3=视频
     */

    private Integer consultType;
    /**
     * 问题描述
     */

    private String questionDesc;
    /**
     * 联系方式
     */

    private String contactInfo;
    /**
     * 预约状态 0=待确认 1=已同意 2=已拒绝 3=已完成 4=已取消
     */

    private Integer status;
    /**
     * 预约申请时间
     */

    private LocalDateTime createTime;
    /**
     * 状态更新时间
     */

    private LocalDateTime updateTime;
    /**
     * 逻辑删除 0=未删除 1=已删除
     */

    private Integer isDelete;
}
