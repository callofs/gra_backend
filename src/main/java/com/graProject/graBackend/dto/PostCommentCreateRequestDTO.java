package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 贴文评论/回复发布请求参数。
 */
@Data
public class PostCommentCreateRequestDTO implements Serializable {

    /**
     * 所属贴文 ID。
     */
    private Long postId;

    /**
     * 父评论 ID（可选；为空表示一级评论）。
     */
    private Long parentCommentId;

    /**
     * 评论内容。
     */
    private String content;

    /**
     * 是否匿名 0=实名 1=匿名。
     */
    private Integer isAnonymous;
}
