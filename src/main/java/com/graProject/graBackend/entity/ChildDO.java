package com.graProject.graBackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 孩子信息表
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_child")
public class ChildDO implements Serializable {
    /**
     * 孩子信息ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 所属家长ID，逻辑关联t_user.id
     */

    @TableField("user_id")
    private Long userId;
    /**
     * 孩子姓名/昵称
     */

    @TableField("child_name")
    private String childName;
    /**
     * 出生日期
     */

    @TableField("birthday")
    private LocalDate birthday;
    /**
     * 孩子性别 0=未知 1=男 2=女
     */

    @TableField("gender")
    private Integer gender;
    /**
     * 就读阶段
     */

    @TableField("grade")
    private String grade;
    /**
     * 成长档案备注
     */

    @TableField("growth_desc")
    private String growthDesc;
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
