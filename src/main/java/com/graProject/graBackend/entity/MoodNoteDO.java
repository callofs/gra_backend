package com.graProject.graBackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("t_mood_note")
public class MoodNoteDO implements Serializable {
    /**
     * 心情笔记ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 所属用户ID，逻辑关联t_user.id
     */

    @TableField("user_id")
    private Long userId;
    /**
     * 心情记录内容
     */

    @TableField("content")
    private String content;
    /**
     * 情绪等级 1-5分
     */

    @TableField("mood_level")
    private Integer moodLevel;
    /**
     * 记录时间
     */

    @TableField("create_time")
    private LocalDateTime createTime;
    /**
     * 更新时间
     */

    @TableField("update_time")
    private LocalDateTime updateTime;
    /**
     * 逻辑删除 0=未删除 1=已删除
     */

    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;
}
