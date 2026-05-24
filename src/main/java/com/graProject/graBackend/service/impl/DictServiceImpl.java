package com.graProject.graBackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graProject.graBackend.dto.DictDTO;
import com.graProject.graBackend.dto.DictCreateRequestDTO;
import com.graProject.graBackend.dto.DictUpdateRequestDTO;
import com.graProject.graBackend.entity.DictDO;
import com.graProject.graBackend.mapper.DictMapper;
import com.graProject.graBackend.service.DictService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统模块（字典）服务实现。
 */
@Service
public class DictServiceImpl implements DictService {

    private final DictMapper dictMapper;

    public DictServiceImpl(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    /**
     * 根据字典类型获取模块列表。
     *
     * @param dictType 字典类型
     * @return 模块列表
     */
    @Override
    public List<DictDTO> listByType(String dictType) {
        if (!StringUtils.hasText(dictType)) {
            return List.of();
        }
        LambdaQueryWrapper<DictDO> wrapper = new LambdaQueryWrapper<DictDO>()
                .eq(DictDO::getDictType, dictType)
                .orderByAsc(DictDO::getSort)
                .orderByAsc(DictDO::getId);
        List<DictDO> records = dictMapper.selectList(wrapper);
        if (records == null || records.isEmpty()) {
            return List.of();
        }
        List<DictDTO> result = new ArrayList<>();
        for (DictDO record : records) {
            if (record == null) {
                continue;
            }
            DictDTO dto = new DictDTO();
            BeanUtils.copyProperties(record, dto);
            result.add(dto);
        }
        return result;
    }

    /**
     * 获取所有模块列表。
     *
     * @return 所有模块列表
     */
    @Override
    public List<DictDTO> listAll() {
        LambdaQueryWrapper<DictDO> wrapper = new LambdaQueryWrapper<DictDO>()
                .orderByAsc(DictDO::getDictType)
                .orderByAsc(DictDO::getSort)
                .orderByAsc(DictDO::getId);
        List<DictDO> records = dictMapper.selectList(wrapper);
        if (records == null || records.isEmpty()) {
            return List.of();
        }
        List<DictDTO> result = new ArrayList<>();
        for (DictDO record : records) {
            if (record == null) {
                continue;
            }
            DictDTO dto = new DictDTO();
            BeanUtils.copyProperties(record, dto);
            result.add(dto);
        }
        return result;
    }

    /**
     * 修改字典项（管理员）。
     *
     * @param id               字典项 ID
     * @param updateRequestDTO 修改参数
     * @return 修改后的字典项
     */
    @Override
    public DictDTO updateDict(Long id, DictUpdateRequestDTO updateRequestDTO) {
        if (id == null) {
            return null;
        }
        DictDO record = dictMapper.selectById(id);
        if (record == null) {
            return null;
        }
        if (updateRequestDTO != null) {
            if (StringUtils.hasText(updateRequestDTO.getDictName())) {
                record.setDictName(updateRequestDTO.getDictName());
            }
            if (updateRequestDTO.getSort() != null) {
                record.setSort(updateRequestDTO.getSort());
            }
        }
        dictMapper.updateById(record);

        DictDTO dto = new DictDTO();
        BeanUtils.copyProperties(record, dto);
        return dto;
    }

    /**
     * 新增字典项（管理员）。
     *
     * @param createRequestDTO 新增参数
     * @return 新增后的字典项
     */
    @Override
    public DictDTO createDict(DictCreateRequestDTO createRequestDTO) {
        if (createRequestDTO == null) {
            throw new IllegalArgumentException("新增参数不能为空");
        }
        if (!StringUtils.hasText(createRequestDTO.getDictType())) {
            throw new IllegalArgumentException("字典类型不能为空");
        }
        if (!StringUtils.hasText(createRequestDTO.getDictCode())) {
            throw new IllegalArgumentException("字典编码不能为空");
        }
        if (!StringUtils.hasText(createRequestDTO.getDictName())) {
            throw new IllegalArgumentException("字典名称不能为空");
        }

        LambdaQueryWrapper<DictDO> existsWrapper = new LambdaQueryWrapper<DictDO>()
                .eq(DictDO::getDictType, createRequestDTO.getDictType())
                .eq(DictDO::getDictCode, createRequestDTO.getDictCode());
        if (dictMapper.selectCount(existsWrapper) > 0) {
            throw new IllegalArgumentException("字典编码已存在");
        }

        DictDO record = new DictDO();
        record.setDictType(createRequestDTO.getDictType());
        record.setDictCode(createRequestDTO.getDictCode());
        record.setDictName(createRequestDTO.getDictName());
        record.setSort(createRequestDTO.getSort());
        dictMapper.insert(record);

        DictDTO dto = new DictDTO();
        BeanUtils.copyProperties(record, dto);
        return dto;
    }
}
