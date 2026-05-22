package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 论坛贴文审核请求参数。
 */
@Data
public class ForumPostAuditRequestDTO implements Serializable {

    /**
     * 贴文 ID。
     */
    private Long postId;

    /**
     * 审核后的贴文状态 1=已发布 2=已驳回。
     */
    private Integer status;
}
