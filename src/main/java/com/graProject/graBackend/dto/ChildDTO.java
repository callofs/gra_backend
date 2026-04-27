package com.graProject.graBackend.dto;

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
public class ChildDTO implements Serializable {
    /**
     * 孩子信息ID
     */

    private Long id;
    /**
     * 所属家长ID，逻辑关联t_user.id
     */

    private Long userId;
    /**
     * 孩子姓名/昵称
     */

    private String childName;
    /**
     * 出生日期
     */

    private LocalDate birthday;
    /**
     * 孩子性别 0=未知 1=男 2=女
     */

    private Integer gender;
    /**
     * 就读阶段
     */

    private String grade;
    /**
     * 成长档案备注
     */

    private String growthDesc;
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
