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
 * 咨询记录表
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_consult_record")
public class ConsultRecordDO implements Serializable {
    /**
     * 咨询记录ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 关联预约ID，逻辑关联t_consult_reservation.id
     */

    @TableField("reservation_id")
    private Long reservationId;
    /**
     * 咨询用户ID，逻辑关联t_user.id
     */

    @TableField("user_id")
    private Long userId;
    /**
     * 解答专家ID，逻辑关联t_user.id
     */

    @TableField("expert_id")
    private Long expertId;
    /**
     * 用户问题详情
     */

    @TableField("question_content")
    private String questionContent;
    /**
     * 专家解答内容
     */

    @TableField("answer_content")
    private String answerContent;
    /**
     * 附件URL
     */

    @TableField("attachment_url")
    private String attachmentUrl;
    /**
     * 是否完成 0=进行中 1=已完成
     */

    @TableField("is_finish")
    private Integer isFinish;
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
