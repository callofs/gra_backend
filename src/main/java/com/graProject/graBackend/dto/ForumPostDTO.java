package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 论坛贴文表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ForumPostDTO implements Serializable {
    /**
     * 贴文ID
     */

    private Long id;
    /**
     * 发布者ID，逻辑关联t_user.id
     */

    private Long userId;

    /**
     * 贴文作者昵称。
     */
    private String authorNickname;

    /**
     * 贴文作者头像（二进制）。
     */
    private byte[] authorAvatar;
    /**
     * 所属板块编码，逻辑关联t_dict.dict_code
     */

    private String sectionCode;

    /**
     * 所属板块名称。
     */

    private String sectionName;
    /**
     * 贴文标题
     */

    private String title;
    /**
     * 贴文正文
     */

    private String content;
    /**
     * 封面图URL
     */

    private String coverImages;
    /**
     * 是否匿名 0=实名 1=匿名
     */

    private Integer isAnonymous;
    /**
     * 是否精华帖 0=普通 1=精华
     */

    private Integer isEssence;
    /**
     * 浏览量
     */

    private Integer viewCount;
    /**
     * 点赞数
     */

    private Integer likeCount;
    /**
     * 评论数
     */

    private Integer commentCount;
    /**
     * 收藏数
     */

    private Integer collectCount;
    /**
     * 当前登录用户是否已收藏。
     */

    private Boolean collected;
    /**
     * 当前登录用户是否已关注作者。
     */

    private Boolean followed;
    /**
     * 贴文状态 0=待审核 1=已发布 2=已驳回 3=已下架
     */

    private Integer status;
    /**
     * 发布时间
     */

    private LocalDateTime createTime;
    /**
     * 更新时间
     */

    private LocalDateTime updateTime;
    /**
     * 逻辑删除 0=未删除 1=已删除
     */

    private Integer isDelete;
}
