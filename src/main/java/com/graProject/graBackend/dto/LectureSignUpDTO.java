package com.graProject.graBackend.dto;

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
public class LectureSignUpDTO implements Serializable {
    /**
     * 报名ID
     */

    private Long id;
    /**
     * 讲座ID，逻辑关联t_expert_lecture.id
     */

    private Long lectureId;
    /**
     * 报名用户ID，逻辑关联t_user.id
     */

    private Long userId;
    /**
     * 是否到场 0=未确认 1=已到场 2=未到场
     */

    private Integer isAttend;
    /**
     * 报名时间
     */

    private LocalDateTime createTime;
}
