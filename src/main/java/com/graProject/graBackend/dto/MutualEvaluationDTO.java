package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 互助信用评价表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MutualEvaluationDTO implements Serializable {
    /**
     * 评价ID
     */

    private Long id;
    /**
     * 关联认领单ID，逻辑关联t_goods_claim.id
     */

    private Long claimId;
    /**
     * 评价人ID，逻辑关联t_user.id
     */

    private Long evaluatorId;
    /**
     * 被评价人ID，逻辑关联t_user.id
     */

    private Long evaluatedId;
    /**
     * 评分 1-5分
     */

    private Integer score;
    /**
     * 评价内容
     */

    private String content;
    /**
     * 评价时间
     */

    private LocalDateTime createTime;
}
