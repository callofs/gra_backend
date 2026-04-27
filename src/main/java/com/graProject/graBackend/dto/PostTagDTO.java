package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 贴文标签表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostTagDTO implements Serializable {
    /**
     * 标签记录ID
     */

    private Long id;
    /**
     * 贴文ID，逻辑关联t_forum_post.id
     */

    private Long postId;
    /**
     * 标签名称
     */

    private String tagName;
    /**
     * 创建时间
     */

    private LocalDateTime createTime;
}
