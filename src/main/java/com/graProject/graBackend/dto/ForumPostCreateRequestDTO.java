package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 发布贴文请求参数。
 */
@Data
public class ForumPostCreateRequestDTO implements Serializable {

    /**
     * 所属板块编码。
     */
    private String sectionCode;

    /**
     * 贴文标题。
     */
    private String title;

    /**
     * 贴文正文（HTML 富文本）。
     */
    private String content;

    /**
     * 封面图 URL（可选）。
     */
    private String coverImages;

    /**
     * 是否匿名 0=实名 1=匿名（可选，默认 0）。
     */
    private Integer isAnonymous;
}
