package com.graProject.graBackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graProject.graBackend.dto.ExpertScheduleDTO;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.entity.ExpertScheduleDO;
import com.graProject.graBackend.mapper.ExpertScheduleMapper;
import com.graProject.graBackend.service.ExpertScheduleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ExpertScheduleServiceImpl implements ExpertScheduleService {

    private final ExpertScheduleMapper expertScheduleMapper;

    public ExpertScheduleServiceImpl(ExpertScheduleMapper expertScheduleMapper) {
        this.expertScheduleMapper = expertScheduleMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExpertScheduleDTO createSchedule(UserDTO loginUser, ExpertScheduleDTO scheduleDTO) {
        Long expertId = requireExpertUserId(loginUser);
        validateCreateOrUpdateRequest(scheduleDTO, false);

        LambdaQueryWrapper<ExpertScheduleDO> duplicateWrapper = new LambdaQueryWrapper<ExpertScheduleDO>()
                .eq(ExpertScheduleDO::getExpertId, expertId)
                .eq(ExpertScheduleDO::getScheduleDate, scheduleDTO.getScheduleDate())
                .eq(ExpertScheduleDO::getTimeSlot, scheduleDTO.getTimeSlot())
                .eq(ExpertScheduleDO::getIsDelete, 0);
        if (expertScheduleMapper.selectCount(duplicateWrapper) > 0) {
            throw new RuntimeException("该时间段排班已存在");
        }

        ExpertScheduleDO scheduleDO = new ExpertScheduleDO();
        BeanUtils.copyProperties(scheduleDTO, scheduleDO);
        scheduleDO.setExpertId(expertId);
        scheduleDO.setReservedCount(0);
        scheduleDO.setStatus(0);
        scheduleDO.setCreateTime(LocalDateTime.now());
        scheduleDO.setUpdateTime(LocalDateTime.now());
        scheduleDO.setIsDelete(0);
        expertScheduleMapper.insert(scheduleDO);

        ExpertScheduleDTO result = new ExpertScheduleDTO();
        BeanUtils.copyProperties(scheduleDO, result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSchedule(UserDTO loginUser, ExpertScheduleDTO scheduleDTO) {
        Long expertId = requireExpertUserId(loginUser);
        validateCreateOrUpdateRequest(scheduleDTO, true);

        ExpertScheduleDO existing = expertScheduleMapper.selectById(scheduleDTO.getId());
        if (existing == null || existing.getIsDelete() == 1) {
            throw new RuntimeException("排班不存在");
        }
        if (!expertId.equals(existing.getExpertId())) {
            throw new RuntimeException("无权修改他人的排班");
        }

        int reservedCount = existing.getReservedCount() == null ? 0 : existing.getReservedCount();
        if (scheduleDTO.getMaxReserveCount() != null && scheduleDTO.getMaxReserveCount() < reservedCount) {
            throw new RuntimeException("最大预约人数不能小于已预约人数");
        }

        LambdaQueryWrapper<ExpertScheduleDO> duplicateWrapper = new LambdaQueryWrapper<ExpertScheduleDO>()
                .eq(ExpertScheduleDO::getExpertId, expertId)
                .eq(ExpertScheduleDO::getScheduleDate, scheduleDTO.getScheduleDate())
                .eq(ExpertScheduleDO::getTimeSlot, scheduleDTO.getTimeSlot())
                .ne(ExpertScheduleDO::getId, scheduleDTO.getId())
                .eq(ExpertScheduleDO::getIsDelete, 0);
        if (expertScheduleMapper.selectCount(duplicateWrapper) > 0) {
            throw new RuntimeException("该时间段排班已存在");
        }

        existing.setScheduleDate(scheduleDTO.getScheduleDate());
        existing.setTimeSlot(scheduleDTO.getTimeSlot());
        existing.setMaxReserveCount(scheduleDTO.getMaxReserveCount());
        if (scheduleDTO.getStatus() != null) {
            existing.setStatus(scheduleDTO.getStatus());
        }
        Integer maxReserveCount = existing.getMaxReserveCount() == null ? 0 : existing.getMaxReserveCount();
        if (maxReserveCount > 0 && reservedCount >= maxReserveCount) {
            existing.setStatus(1);
        }
        existing.setUpdateTime(LocalDateTime.now());
        return expertScheduleMapper.updateById(existing) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSchedule(UserDTO loginUser, Long scheduleId) {
        Long expertId = requireExpertUserId(loginUser);
        ExpertScheduleDO existing = expertScheduleMapper.selectById(scheduleId);
        if (existing == null || existing.getIsDelete() == 1) {
            throw new RuntimeException("排班不存在");
        }
        if (!expertId.equals(existing.getExpertId())) {
            throw new RuntimeException("无权删除他人的排班");
        }
        int reservedCount = existing.getReservedCount() == null ? 0 : existing.getReservedCount();
        if (reservedCount > 0) {
            throw new RuntimeException("该排班已有预约，无法删除");
        }
        existing.setIsDelete(1);
        existing.setUpdateTime(LocalDateTime.now());
        return expertScheduleMapper.updateById(existing) > 0;
    }

    @Override
    public IPage<ExpertScheduleDTO> listMySchedules(UserDTO loginUser, long page, long size) {
        Long expertId = requireExpertUserId(loginUser);
        Page<ExpertScheduleDO> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ExpertScheduleDO> wrapper = new LambdaQueryWrapper<ExpertScheduleDO>()
                .eq(ExpertScheduleDO::getExpertId, expertId)
                .eq(ExpertScheduleDO::getIsDelete, 0)
                .orderByDesc(ExpertScheduleDO::getScheduleDate)
                .orderByDesc(ExpertScheduleDO::getId);
        return expertScheduleMapper.selectPage(pageParam, wrapper).convert(scheduleDO -> {
            ExpertScheduleDTO dto = new ExpertScheduleDTO();
            if (scheduleDO != null) {
                BeanUtils.copyProperties(scheduleDO, dto);
            }
            return dto;
        });
    }

    private Long requireExpertUserId(UserDTO loginUser) {
        if (loginUser == null || loginUser.getId() == null) {
            throw new RuntimeException("用户未登录");
        }
        if (loginUser.getRole() == null || loginUser.getRole() != 2) {
            throw new RuntimeException("只有专家可以管理排班");
        }
        return loginUser.getId();
    }

    private void validateCreateOrUpdateRequest(ExpertScheduleDTO scheduleDTO, boolean requireId) {
        if (scheduleDTO == null) {
            throw new RuntimeException("排班信息不能为空");
        }
        if (requireId && scheduleDTO.getId() == null) {
            throw new RuntimeException("排班ID不能为空");
        }
        if (scheduleDTO.getScheduleDate() == null) {
            throw new RuntimeException("排班日期不能为空");
        }
        if (scheduleDTO.getTimeSlot() == null || scheduleDTO.getTimeSlot().isBlank()) {
            throw new RuntimeException("时间段不能为空");
        }
        if (scheduleDTO.getMaxReserveCount() == null || scheduleDTO.getMaxReserveCount() <= 0) {
            throw new RuntimeException("最大预约人数必须大于0");
        }
        if (scheduleDTO.getStatus() != null && scheduleDTO.getStatus() != 0 && scheduleDTO.getStatus() != 1 && scheduleDTO.getStatus() != 2) {
            throw new RuntimeException("排班状态不合法");
        }
    }
}
