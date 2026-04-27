package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 贴文收藏表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostCollectionDTO implements Serializable {
    /**
     * 收藏ID
     */

    private Long id;
    /**
     * 收藏用户ID，逻辑关联t_user.id
     */

    private Long userId;
    /**
     * 收藏贴文ID，逻辑关联t_forum_post.id
     */

    private Long postId;
    /**
     * 自定义标签
     */

    private String customTag;
    /**
     * 收藏备注
     */

    private String remark;
    /**
     * 收藏时间
     */

    private LocalDateTime createTime;
}
