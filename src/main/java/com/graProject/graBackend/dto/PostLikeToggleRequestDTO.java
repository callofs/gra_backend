package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 贴文/评论点赞或取消点赞请求参数。
 */
@Data
public class PostLikeToggleRequestDTO implements Serializable {

    /**
     * 点赞类型 1=贴文点赞 2=评论点赞。
     */
    private Integer likeType;

    /**
     * 关联ID（贴文ID/评论ID）。
     */
    private Long relateId;
}
