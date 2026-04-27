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
 * 咨询预约表
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_consult_reservation")
public class ConsultReservationDO implements Serializable {
    /**
     * 预约ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 预约用户ID，逻辑关联t_user.id
     */

    @TableField("user_id")
    private Long userId;
    /**
     * 被预约专家ID，逻辑关联t_user.id
     */

    @TableField("expert_id")
    private Long expertId;
    /**
     * 关联排班ID，逻辑关联t_expert_schedule.id
     */

    @TableField("schedule_id")
    private Long scheduleId;
    /**
     * 咨询类型 1=图文 2=语音 3=视频
     */

    @TableField("consult_type")
    private Integer consultType;
    /**
     * 问题描述
     */

    @TableField("question_desc")
    private String questionDesc;
    /**
     * 联系方式
     */

    @TableField("contact_info")
    private String contactInfo;
    /**
     * 预约状态 0=待确认 1=已同意 2=已拒绝 3=已完成 4=已取消
     */

    @TableField("status")
    private Integer status;
    /**
     * 预约申请时间
     */

    @TableField("create_time")
    private LocalDateTime createTime;
    /**
     * 状态更新时间
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
