package com.graProject.graBackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 讲座报名表
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_lecture_sign_up")
public class LectureSignUpDO implements Serializable {
    /**
     * 报名ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 讲座ID，逻辑关联t_expert_lecture.id
     */

    @TableField("lecture_id")
    private Long lectureId;
    /**
     * 报名用户ID，逻辑关联t_user.id
     */

    @TableField("user_id")
    private Long userId;
    /**
     * 是否到场 0=未确认 1=已到场 2=未到场
     */

    @TableField("is_attend")
    private Integer isAttend;
    /**
     * 报名时间
     */

    @TableField("create_time")
    private LocalDateTime createTime;
}
