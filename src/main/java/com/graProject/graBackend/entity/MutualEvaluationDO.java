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
 * 互助信用评价表
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_mutual_evaluation")
public class MutualEvaluationDO implements Serializable {
    /**
     * 评价ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 关联认领单ID，逻辑关联t_goods_claim.id
     */

    @TableField("claim_id")
    private Long claimId;
    /**
     * 评价人ID，逻辑关联t_user.id
     */

    @TableField("evaluator_id")
    private Long evaluatorId;
    /**
     * 被评价人ID，逻辑关联t_user.id
     */

    @TableField("evaluated_id")
    private Long evaluatedId;
    /**
     * 评分 1-5分
     */

    @TableField("score")
    private Integer score;
    /**
     * 评价内容
     */

    @TableField("content")
    private String content;
    /**
     * 评价时间
     */

    @TableField("create_time")
    private LocalDateTime createTime;
}
