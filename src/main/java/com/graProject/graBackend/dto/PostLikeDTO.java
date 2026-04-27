package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 贴文点赞表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostLikeDTO implements Serializable {
    /**
     * 点赞ID
     */

    private Long id;
    /**
     * 点赞类型 1=贴文点赞 2=评论点赞
     */

    private Integer likeType;
    /**
     * 关联ID（贴文ID/评论ID）
     */

    private Long relateId;
    /**
     * 点赞用户ID，逻辑关联t_user.id
     */

    private Long userId;
    /**
     * 点赞时间
     */

    private LocalDateTime createTime;
}
