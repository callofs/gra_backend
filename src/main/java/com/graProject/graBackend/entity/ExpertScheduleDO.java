package com.graProject.graBackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 专家排班表
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_expert_schedule")
public class ExpertScheduleDO implements Serializable {
    /**
     * 排班ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 专家用户ID，逻辑关联t_user.id
     */

    @TableField("expert_id")
    private Long expertId;
    /**
     * 排班日期
     */

    @TableField("schedule_date")
    private LocalDate scheduleDate;
    /**
     * 预约时间段
     */

    @TableField("time_slot")
    private String timeSlot;
    /**
     * 最大可预约人数
     */

    @TableField("max_reserve_count")
    private Integer maxReserveCount;
    /**
     * 已预约人数
     */

    @TableField("reserved_count")
    private Integer reservedCount;
    /**
     * 排班状态 0=可预约 1=已满 2=已关闭
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
