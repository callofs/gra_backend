package com.graProject.graBackend.service;

import com.graProject.graBackend.dto.DictDTO;
import com.graProject.graBackend.dto.DictCreateRequestDTO;
import com.graProject.graBackend.dto.DictUpdateRequestDTO;

import java.util.List;

/**
 * 系统模块（字典）服务。
 */
public interface DictService {

    /**
     * 根据字典类型获取模块列表。
     *
     * @param dictType 字典类型
     * @return 模块列表
     */
    List<DictDTO> listByType(String dictType);

    /**
     * 修改字典项（管理员）。
     *
     * @param id               字典项 ID
     * @param updateRequestDTO 修改参数
     * @return 修改后的字典项
     */
    DictDTO updateDict(Long id, DictUpdateRequestDTO updateRequestDTO);

    /**
     * 新增字典项（管理员）。
     *
     * @param createRequestDTO 新增参数
     * @return 新增后的字典项
     */
    DictDTO createDict(DictCreateRequestDTO createRequestDTO);
}
