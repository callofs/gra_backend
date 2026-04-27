package com.graProject.graBackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 系统字典表
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_dict")
public class DictDO implements Serializable {
    /**
     * 字典ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 字典类型
     */

    @TableField("dict_type")
    private String dictType;
    /**
     * 字典编码
     */

    @TableField("dict_code")
    private String dictCode;
    /**
     * 字典显示名称
     */

    @TableField("dict_name")
    private String dictName;
    /**
     * 排序序号
     */

    @TableField("sort")
    private Integer sort;
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
}
