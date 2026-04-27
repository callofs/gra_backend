package com.graProject.graBackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 热点排行统计表
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_hot_rank")
public class HotRankDO implements Serializable {
    /**
     * 排行ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 排行类型 1=日榜 2=周榜 3=月榜
     */

    @TableField("rank_type")
    private Integer rankType;
    /**
     * 关联贴文ID，逻辑关联t_forum_post.id
     */

    @TableField("relate_id")
    private Long relateId;
    /**
     * rankScore
     */

    @TableField("rank_score")
    private BigDecimal rankScore;
    /**
     * 排行名次
     */

    @TableField("rank_num")
    private Integer rankNum;
    /**
     * 统计日期
     */

    @TableField("stat_date")
    private LocalDate statDate;
    /**
     * 创建时间
     */

    @TableField("create_time")
    private LocalDateTime createTime;
}
