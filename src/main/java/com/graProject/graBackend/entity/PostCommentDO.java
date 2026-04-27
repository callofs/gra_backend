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
 * 贴文评论表
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_post_comment")
public class PostCommentDO implements Serializable {
    /**
     * 评论ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 所属贴文ID，逻辑关联t_forum_post.id
     */

    @TableField("post_id")
    private Long postId;
    /**
     * 评论者ID，逻辑关联t_user.id
     */

    @TableField("user_id")
    private Long userId;
    /**
     * 父评论ID，二级回复关联
     */

    @TableField("parent_comment_id")
    private Long parentCommentId;
    /**
     * 评论内容
     */

    @TableField("content")
    private String content;
    /**
     * 是否匿名 0=实名 1=匿名
     */

    @TableField("is_anonymous")
    private Integer isAnonymous;
    /**
     * 点赞数
     */

    @TableField("like_count")
    private Integer likeCount;
    /**
     * 评论时间
     */

    @TableField("create_time")
    private LocalDateTime createTime;
    /**
     * 逻辑删除 0=未删除 1=已删除
     */

    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;
}
