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
 * 育儿心得笔记表
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_parenting_note")
public class ParentingNoteDO implements Serializable {
    /**
     * 笔记ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 所属用户ID，逻辑关联t_user.id
     */

    @TableField("user_id")
    private Long userId;
    /**
     * 关联孩子ID，逻辑关联t_child.id
     */

    @TableField("child_id")
    private Long childId;
    /**
     * 笔记标题
     */

    @TableField("title")
    private String title;
    /**
     * 笔记模板类型
     */

    @TableField("note_type")
    private String noteType;
    /**
     * 笔记内容
     */

    @TableField("content")
    private String content;
    /**
     * 是否公开 0=私密 1=公开
     */

    @TableField("is_public")
    private Integer isPublic;
    /**
     * 创建时间
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
