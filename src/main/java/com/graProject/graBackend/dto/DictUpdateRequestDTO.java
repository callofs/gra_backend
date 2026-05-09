package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 系统模块（字典项）修改请求参数。
 */
@Data
public class DictUpdateRequestDTO implements Serializable {

    /**
     * 字典显示名称。
     */
    private String dictName;

    /**
     * 排序序号。
     */
    private Integer sort;
}
