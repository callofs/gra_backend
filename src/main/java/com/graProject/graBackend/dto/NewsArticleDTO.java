package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 资讯文章表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NewsArticleDTO implements Serializable {
    /**
     * 文章ID
     */

    private Long id;
    /**
     * 作者类型 1=系统官方 2=认证专家
     */

    private Integer authorType;
    /**
     * 作者ID，专家逻辑关联t_user.id
     */

    private Long authorId;
    /**
     * 文章标题
     */

    private String title;
    /**
     * 文章封面图URL
     */

    private String cover;
    /**
     * 文章正文
     */

    private String content;
    /**
     * 文章类型 1=育儿资讯 2=科普文章
     */

    private Integer articleType;
    /**
     * 文章标签
     */

    private String tag;
    /**
     * 浏览量
     */

    private Integer viewCount;
    /**
     * 点赞数
     */

    private Integer likeCount;
    /**
     * 收藏数
     */

    private Integer collectCount;
    /**
     * 是否置顶 0=不置顶 1=置顶
     */

    private Integer isTop;
    /**
     * 发布状态 0=草稿 1=已发布 2=已下架
     */

    private Integer status;
    /**
     * 发布时间
     */

    private LocalDateTime publishTime;
    /**
     * 更新时间
     */

    private LocalDateTime updateTime;
    /**
     * 逻辑删除 0=未删除 1=已删除
     */

    private Integer isDelete;
}
