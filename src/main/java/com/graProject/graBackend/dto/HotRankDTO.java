package com.graProject.graBackend.dto;

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
public class HotRankDTO implements Serializable {
    /**
     * 排行ID
     */

    private Long id;
    /**
     * 排行类型 1=日榜 2=周榜 3=月榜
     */

    private Integer rankType;
    /**
     * 关联贴文ID，逻辑关联t_forum_post.id
     */

    private Long relateId;
    /**
     * rankScore
     */

    private BigDecimal rankScore;
    /**
     * 排行名次
     */

    private Integer rankNum;
    /**
     * 统计日期
     */

    private LocalDate statDate;
    /**
     * 创建时间
     */

    private LocalDateTime createTime;
}
