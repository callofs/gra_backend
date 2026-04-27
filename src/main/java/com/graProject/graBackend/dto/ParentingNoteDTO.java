package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 育儿心得笔记表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ParentingNoteDTO implements Serializable {
    /**
     * 笔记ID
     */

    private Long id;
    /**
     * 所属用户ID，逻辑关联t_user.id
     */

    private Long userId;
    /**
     * 关联孩子ID，逻辑关联t_child.id
     */

    private Long childId;
    /**
     * 笔记标题
     */

    private String title;
    /**
     * 笔记模板类型
     */

    private String noteType;
    /**
     * 笔记内容
     */

    private String content;
    /**
     * 是否公开 0=私密 1=公开
     */

    private Integer isPublic;
    /**
     * 创建时间
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
