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
 * 资讯文章表
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_news_article")
public class NewsArticleDO implements Serializable {
    /**
     * 文章ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 作者类型 1=系统官方 2=认证专家
     */

    @TableField("author_type")
    private Integer authorType;
    /**
     * 作者ID，专家逻辑关联t_user.id
     */

    @TableField("author_id")
    private Long authorId;
    /**
     * 文章标题
     */

    @TableField("title")
    private String title;
    /**
     * 文章封面图URL
     */

    @TableField("cover")
    private String cover;
    /**
     * 文章正文
     */

    @TableField("content")
    private String content;
    /**
     * 文章类型 1=育儿资讯 2=科普文章
     */

    @TableField("article_type")
    private Integer articleType;
    /**
     * 文章标签
     */

    @TableField("tag")
    private String tag;
    /**
     * 浏览量
     */

    @TableField("view_count")
    private Integer viewCount;
    /**
     * 点赞数
     */

    @TableField("like_count")
    private Integer likeCount;
    /**
     * 收藏数
     */

    @TableField("collect_count")
    private Integer collectCount;
    /**
     * 是否置顶 0=不置顶 1=置顶
     */

    @TableField("is_top")
    private Integer isTop;
    /**
     * 发布状态 0=草稿 1=已发布 2=已下架
     */

    @TableField("status")
    private Integer status;
    /**
     * 发布时间
     */

    @TableField("publish_time")
    private LocalDateTime publishTime;
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
