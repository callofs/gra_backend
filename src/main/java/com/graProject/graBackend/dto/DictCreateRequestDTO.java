package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 系统模块（字典项）新增请求参数。
 */
@Data
public class DictCreateRequestDTO implements Serializable {

    /**
     * 字典类型。
     */
    private String dictType;

    /**
     * 字典编码。
     */
    private String dictCode;

    /**
     * 字典显示名称。
     */
    private String dictName;

    /**
     * 排序序号。
     */
    private Integer sort;
}
