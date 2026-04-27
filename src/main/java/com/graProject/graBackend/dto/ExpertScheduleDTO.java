package com.graProject.graBackend.dto;

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
public class ExpertScheduleDTO implements Serializable {
    /**
     * 排班ID
     */

    private Long id;
    /**
     * 专家用户ID，逻辑关联t_user.id
     */

    private Long expertId;
    /**
     * 排班日期
     */

    private LocalDate scheduleDate;
    /**
     * 预约时间段
     */

    private String timeSlot;
    /**
     * 最大可预约人数
     */

    private Integer maxReserveCount;
    /**
     * 已预约人数
     */

    private Integer reservedCount;
    /**
     * 排班状态 0=可预约 1=已满 2=已关闭
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
