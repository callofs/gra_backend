package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 用户浏览记录表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserBrowseDTO implements Serializable {
    /**
     * 浏览记录ID
     */

    private Long id;
    /**
     * 浏览用户ID，逻辑关联t_user.id
     */

    private Long userId;
    /**
     * 浏览类型 1=论坛贴文 2=资讯文章
     */

    private Integer browseType;
    /**
     * 关联内容ID（贴文ID/文章ID）
     */

    private Long relateId;
    /**
     * 内容标签
     */

    private String tag;
    /**
     * 停留时长（秒）
     */

    private Integer stayDuration;
    /**
     * 浏览时间
     */

    private LocalDateTime browseTime;
}
