package com.graProject.graBackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graProject.graBackend.dto.ConsultReservationDTO;
import com.graProject.graBackend.dto.ExpertScheduleDTO;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.entity.ConsultReservationDO;
import com.graProject.graBackend.entity.ExpertScheduleDO;
import com.graProject.graBackend.entity.UserDO;
import com.graProject.graBackend.mapper.ConsultReservationMapper;
import com.graProject.graBackend.mapper.ExpertScheduleMapper;
import com.graProject.graBackend.mapper.UserMapper;
import com.graProject.graBackend.service.ConsultReservationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsultReservationServiceImpl implements ConsultReservationService {

    private final ConsultReservationMapper consultReservationMapper;
    private final ExpertScheduleMapper expertScheduleMapper;
    private final UserMapper userMapper;

    public ConsultReservationServiceImpl(ConsultReservationMapper consultReservationMapper,
            ExpertScheduleMapper expertScheduleMapper,
            UserMapper userMapper) {
        this.consultReservationMapper = consultReservationMapper;
        this.expertScheduleMapper = expertScheduleMapper;
        this.userMapper = userMapper;
    }

    @Override
    public List<ExpertScheduleDTO> listExpertSchedules(Long expertId) {
        LambdaQueryWrapper<ExpertScheduleDO> wrapper = new LambdaQueryWrapper<ExpertScheduleDO>()
                .eq(ExpertScheduleDO::getExpertId, expertId)
                .eq(ExpertScheduleDO::getIsDelete, 0)
                .ge(ExpertScheduleDO::getScheduleDate, LocalDate.now())
                .orderByAsc(ExpertScheduleDO::getScheduleDate)
                .orderByAsc(ExpertScheduleDO::getId);
        List<ExpertScheduleDO> scheduleDOS = expertScheduleMapper.selectList(wrapper);
        return scheduleDOS.stream().map(scheduleDO -> {
            ExpertScheduleDTO dto = new ExpertScheduleDTO();
            if (scheduleDO != null) {
                BeanUtils.copyProperties(scheduleDO, dto);
            }
            return dto;
        }).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConsultReservationDTO createReservation(UserDTO loginUser, ConsultReservationDTO reservationDTO) {
        Long userId = requireLoginUserId(loginUser);
        validateReservationRequest(reservationDTO);

        UserDO expert = userMapper.selectById(reservationDTO.getExpertId());
        if (expert == null || expert.getIsDelete() == 1 || expert.getRole() == null || expert.getRole() != 2) {
            throw new RuntimeException("专家不存在");
        }

        ExpertScheduleDO scheduleDO = expertScheduleMapper.selectById(reservationDTO.getScheduleId());
        if (scheduleDO == null || scheduleDO.getIsDelete() == 1) {
            throw new RuntimeException("排班不存在");
        }
        if (!reservationDTO.getExpertId().equals(scheduleDO.getExpertId())) {
            throw new RuntimeException("排班与专家不匹配");
        }
        if (scheduleDO.getStatus() == null || scheduleDO.getStatus() != 0) {
            throw new RuntimeException("该时间段不可预约");
        }
        if (scheduleDO.getMaxReserveCount() != null && scheduleDO.getReservedCount() != null
                && scheduleDO.getReservedCount() >= scheduleDO.getMaxReserveCount()) {
            throw new RuntimeException("该时间段预约已满");
        }

        LambdaQueryWrapper<ConsultReservationDO> duplicateWrapper = new LambdaQueryWrapper<ConsultReservationDO>()
                .eq(ConsultReservationDO::getUserId, userId)
                .eq(ConsultReservationDO::getScheduleId, reservationDTO.getScheduleId())
                .in(ConsultReservationDO::getStatus, List.of(0, 1));
        if (consultReservationMapper.selectCount(duplicateWrapper) > 0) {
            throw new RuntimeException("您已预约该时间段");
        }

        ConsultReservationDO reservationDO = new ConsultReservationDO();
        BeanUtils.copyProperties(reservationDTO, reservationDO);
        reservationDO.setUserId(userId);
        reservationDO.setStatus(0);
        reservationDO.setCreateTime(LocalDateTime.now());
        reservationDO.setUpdateTime(LocalDateTime.now());
        reservationDO.setIsDelete(0);
        consultReservationMapper.insert(reservationDO);

        Integer reservedCount = scheduleDO.getReservedCount() == null ? 0 : scheduleDO.getReservedCount();
        Integer maxReserveCount = scheduleDO.getMaxReserveCount() == null ? 0 : scheduleDO.getMaxReserveCount();
        scheduleDO.setReservedCount(reservedCount + 1);
        if (maxReserveCount > 0 && scheduleDO.getReservedCount() >= maxReserveCount) {
            scheduleDO.setStatus(1);
        }
        scheduleDO.setUpdateTime(LocalDateTime.now());
        expertScheduleMapper.updateById(scheduleDO);

        ConsultReservationDTO result = new ConsultReservationDTO();
        BeanUtils.copyProperties(reservationDO, result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelReservation(UserDTO loginUser, Long reservationId) {
        Long userId = requireLoginUserId(loginUser);
        ConsultReservationDO reservationDO = consultReservationMapper.selectById(reservationId);
        if (reservationDO == null || reservationDO.getIsDelete() == 1) {
            throw new RuntimeException("预约不存在");
        }
        if (!userId.equals(reservationDO.getUserId())) {
            throw new RuntimeException("无权取消他人的预约");
        }
        if (reservationDO.getStatus() != null && reservationDO.getStatus() == 4) {
            return true;
        }

        reservationDO.setStatus(4);
        reservationDO.setUpdateTime(LocalDateTime.now());
        int updated = consultReservationMapper.updateById(reservationDO);
        rollbackScheduleReserveCount(reservationDO.getScheduleId());
        return updated > 0;
    }

    @Override
    public IPage<ConsultReservationDTO> listMyReservations(UserDTO loginUser, long page, long size) {
        Long userId = requireLoginUserId(loginUser);
        Page<ConsultReservationDO> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ConsultReservationDO> wrapper = new LambdaQueryWrapper<ConsultReservationDO>()
                .eq(ConsultReservationDO::getUserId, userId)
                .eq(ConsultReservationDO::getIsDelete, 0)
                .orderByDesc(ConsultReservationDO::getCreateTime)
                .orderByDesc(ConsultReservationDO::getId);
        return consultReservationMapper.selectPage(pageParam, wrapper).convert(this::buildReservationDTO);
    }

    @Override
    public IPage<ConsultReservationDTO> listExpertReservations(UserDTO loginUser, long page, long size) {
        Long expertId = requireLoginUserId(loginUser);
        if (loginUser.getRole() == null || loginUser.getRole() != 2) {
            throw new RuntimeException("只有专家可以查看预约列表");
        }
        Page<ConsultReservationDO> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ConsultReservationDO> wrapper = new LambdaQueryWrapper<ConsultReservationDO>()
                .eq(ConsultReservationDO::getExpertId, expertId)
                .eq(ConsultReservationDO::getIsDelete, 0)
                .orderByDesc(ConsultReservationDO::getCreateTime)
                .orderByDesc(ConsultReservationDO::getId);
        return consultReservationMapper.selectPage(pageParam, wrapper).convert(this::buildReservationDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateReservationStatus(UserDTO loginUser, Long reservationId, Integer status) {
        Long expertId = requireLoginUserId(loginUser);
        if (loginUser.getRole() == null || loginUser.getRole() != 2) {
            throw new RuntimeException("只有专家可以处理预约");
        }
        if (status == null || (status != 1 && status != 2 && status != 3)) {
            throw new RuntimeException("状态值不合法");
        }

        ConsultReservationDO reservationDO = consultReservationMapper.selectById(reservationId);
        if (reservationDO == null || reservationDO.getIsDelete() == 1) {
            throw new RuntimeException("预约不存在");
        }
        if (!expertId.equals(reservationDO.getExpertId())) {
            throw new RuntimeException("无权处理该预约");
        }
        if (reservationDO.getStatus() != null && reservationDO.getStatus() == 4) {
            throw new RuntimeException("该预约已取消");
        }

        Integer oldStatus = reservationDO.getStatus();
        reservationDO.setStatus(status);
        reservationDO.setUpdateTime(LocalDateTime.now());
        int updated = consultReservationMapper.updateById(reservationDO);
        if (updated > 0 && status == 2 && oldStatus != null && (oldStatus == 0 || oldStatus == 1)) {
            rollbackScheduleReserveCount(reservationDO.getScheduleId());
        }
        return updated > 0;
    }

    private ConsultReservationDTO buildReservationDTO(ConsultReservationDO reservationDO) {
        ConsultReservationDTO dto = new ConsultReservationDTO();
        if (reservationDO != null) {
            BeanUtils.copyProperties(reservationDO, dto);
        }
        return dto;
    }

    private Long requireLoginUserId(UserDTO loginUser) {
        if (loginUser == null || loginUser.getId() == null) {
            throw new RuntimeException("用户未登录");
        }
        return loginUser.getId();
    }

    private void validateReservationRequest(ConsultReservationDTO reservationDTO) {
        if (reservationDTO == null) {
            throw new RuntimeException("预约信息不能为空");
        }
        if (reservationDTO.getExpertId() == null) {
            throw new RuntimeException("专家ID不能为空");
        }
        if (reservationDTO.getScheduleId() == null) {
            throw new RuntimeException("排班ID不能为空");
        }
        if (reservationDTO.getConsultType() == null || reservationDTO.getConsultType() < 1
                || reservationDTO.getConsultType() > 3) {
            throw new RuntimeException("咨询类型不合法");
        }
        if (reservationDTO.getQuestionDesc() == null || reservationDTO.getQuestionDesc().isBlank()) {
            throw new RuntimeException("问题描述不能为空");
        }
        if (reservationDTO.getContactInfo() == null || reservationDTO.getContactInfo().isBlank()) {
            throw new RuntimeException("联系方式不能为空");
        }
    }

    private void rollbackScheduleReserveCount(Long scheduleId) {
        if (scheduleId == null) {
            return;
        }
        ExpertScheduleDO scheduleDO = expertScheduleMapper.selectById(scheduleId);
        if (scheduleDO == null || scheduleDO.getIsDelete() == 1) {
            return;
        }
        int reservedCount = scheduleDO.getReservedCount() == null ? 0 : scheduleDO.getReservedCount();
        scheduleDO.setReservedCount(Math.max(0, reservedCount - 1));
        if (scheduleDO.getStatus() != null && scheduleDO.getStatus() == 1) {
            scheduleDO.setStatus(0);
        }
        scheduleDO.setUpdateTime(LocalDateTime.now());
        expertScheduleMapper.updateById(scheduleDO);
    }
}
