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
 * 论坛贴文表
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_forum_post")
public class ForumPostDO implements Serializable {
    /**
     * 贴文ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 发布者ID，逻辑关联t_user.id
     */

    @TableField("user_id")
    private Long userId;
    /**
     * 所属板块编码，逻辑关联t_dict.dict_code
     */

    @TableField("section_code")
    private String sectionCode;
    /**
     * 贴文标题
     */

    @TableField("title")
    private String title;
    /**
     * 贴文正文
     */

    @TableField("content")
    private String content;
    /**
     * 封面图URL
     */

    @TableField("cover_images")
    private String coverImages;
    /**
     * 是否匿名 0=实名 1=匿名
     */

    @TableField("is_anonymous")
    private Integer isAnonymous;
    /**
     * 是否精华帖 0=普通 1=精华
     */

    @TableField("is_essence")
    private Integer isEssence;
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
     * 评论数
     */

    @TableField("comment_count")
    private Integer commentCount;
    /**
     * 收藏数
     */

    @TableField("collect_count")
    private Integer collectCount;
    /**
     * 贴文状态 0=待审核 1=已发布 2=已驳回 3=已下架
     */

    @TableField("status")
    private Integer status;
    /**
     * 发布时间
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
