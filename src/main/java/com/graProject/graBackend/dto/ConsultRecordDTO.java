package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 咨询记录表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConsultRecordDTO implements Serializable {
    /**
     * 咨询记录ID
     */

    private Long id;
    /**
     * 关联预约ID，逻辑关联t_consult_reservation.id
     */

    private Long reservationId;
    /**
     * 咨询用户ID，逻辑关联t_user.id
     */

    private Long userId;
    /**
     * 解答专家ID，逻辑关联t_user.id
     */

    private Long expertId;
    /**
     * 用户问题详情
     */

    private String questionContent;
    /**
     * 专家解答内容
     */

    private String answerContent;
    /**
     * 附件URL
     */

    private String attachmentUrl;
    /**
     * 是否完成 0=进行中 1=已完成
     */

    private Integer isFinish;
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
