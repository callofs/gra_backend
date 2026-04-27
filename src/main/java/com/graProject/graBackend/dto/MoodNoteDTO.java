package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 宝妈心情笔记表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MoodNoteDTO implements Serializable {
    /**
     * 心情笔记ID
     */

    private Long id;
    /**
     * 所属用户ID，逻辑关联t_user.id
     */

    private Long userId;
    /**
     * 心情记录内容
     */

    private String content;
    /**
     * 情绪等级 1-5分
     */

    private Integer moodLevel;
    /**
     * 记录时间
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
