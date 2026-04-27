package com.graProject.graBackend.dto;

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
public class PostCommentDTO implements Serializable {
    /**
     * 评论ID
     */

    private Long id;
    /**
     * 所属贴文ID，逻辑关联t_forum_post.id
     */

    private Long postId;
    /**
     * 评论者ID，逻辑关联t_user.id
     */

    private Long userId;
    /**
     * 父评论ID，二级回复关联
     */

    private Long parentCommentId;
    /**
     * 评论内容
     */

    private String content;
    /**
     * 是否匿名 0=实名 1=匿名
     */

    private Integer isAnonymous;
    /**
     * 点赞数
     */

    private Integer likeCount;
    /**
     * 评论时间
     */

    private LocalDateTime createTime;
    /**
     * 逻辑删除 0=未删除 1=已删除
     */

    private Integer isDelete;
}
