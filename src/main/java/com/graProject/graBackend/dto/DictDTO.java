package com.graProject.graBackend.dto;

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
public class DictDTO implements Serializable {
    /**
     * 字典ID
     */

    private Long id;
    /**
     * 字典类型
     */

    private String dictType;
    /**
     * 字典编码
     */

    private String dictCode;
    /**
     * 字典显示名称
     */

    private String dictName;
    /**
     * 排序序号
     */

    private Integer sort;
    /**
     * 创建时间
     */

    private LocalDateTime createTime;
    /**
     * 更新时间
     */

    private LocalDateTime updateTime;
}
