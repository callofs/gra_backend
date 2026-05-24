package com.graProject.graBackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graProject.graBackend.dto.ExpertLectureDTO;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.entity.ExpertLectureDO;
import com.graProject.graBackend.mapper.ExpertLectureMapper;
import com.graProject.graBackend.service.ExpertLectureService;
import com.graProject.graBackend.service.LectureSignUpService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Service
public class ExpertLectureServiceImpl implements ExpertLectureService {

    private final ExpertLectureMapper expertLectureMapper;
    private final LectureSignUpService lectureSignUpService;

    public ExpertLectureServiceImpl(ExpertLectureMapper expertLectureMapper,
            LectureSignUpService lectureSignUpService) {
        this.expertLectureMapper = expertLectureMapper;
        this.lectureSignUpService = lectureSignUpService;
    }

    @Override
    public ExpertLectureDTO createLecture(UserDTO expertUser, ExpertLectureDTO lectureDTO) {
        if (expertUser == null || expertUser.getId() == null) {
            throw new RuntimeException("用户未登录");
        }
        if (expertUser.getRole() != 2) {
            throw new RuntimeException("只有专家才能创建讲座");
        }

        ExpertLectureDO lectureDO = new ExpertLectureDO();
        BeanUtils.copyProperties(lectureDTO, lectureDO);
        lectureDO.setExpertId(expertUser.getId());
        lectureDO.setSignUpCount(0);
        lectureDO.setStatus(0);
        lectureDO.setCreateTime(LocalDateTime.now());
        lectureDO.setUpdateTime(LocalDateTime.now());
        lectureDO.setIsDelete(0);

        expertLectureMapper.insert(lectureDO);

        ExpertLectureDTO result = new ExpertLectureDTO();
        BeanUtils.copyProperties(lectureDO, result);
        return result;
    }

    @Override
    public boolean updateLecture(UserDTO expertUser, ExpertLectureDTO lectureDTO) {
        if (expertUser == null || expertUser.getId() == null) {
            throw new RuntimeException("用户未登录");
        }

        ExpertLectureDO existing = expertLectureMapper.selectById(lectureDTO.getId());
        if (existing == null) {
            throw new RuntimeException("讲座不存在");
        }
        if (!existing.getExpertId().equals(expertUser.getId())) {
            throw new RuntimeException("无权修改他人的讲座");
        }

        existing.setTitle(lectureDTO.getTitle());
        existing.setCover(lectureDTO.getCover());
        existing.setDescription(lectureDTO.getDescription());
        existing.setLectureTime(lectureDTO.getLectureTime());
        existing.setLiveUrl(lectureDTO.getLiveUrl());
        existing.setReplayUrl(lectureDTO.getReplayUrl());
        existing.setAttachmentUrl(lectureDTO.getAttachmentUrl());
        existing.setMaxSignUp(lectureDTO.getMaxSignUp());
        existing.setUpdateTime(LocalDateTime.now());

        return expertLectureMapper.updateById(existing) > 0;
    }

    @Override
    public boolean deleteLecture(UserDTO expertUser, Long lectureId) {
        if (expertUser == null || expertUser.getId() == null) {
            throw new RuntimeException("用户未登录");
        }

        ExpertLectureDO existing = expertLectureMapper.selectById(lectureId);
        if (existing == null) {
            throw new RuntimeException("讲座不存在");
        }
        if (!existing.getExpertId().equals(expertUser.getId())) {
            throw new RuntimeException("无权删除他人的讲座");
        }

        existing.setIsDelete(1);
        existing.setUpdateTime(LocalDateTime.now());
        return expertLectureMapper.updateById(existing) > 0;
    }

    @Override
    public ExpertLectureDTO getLectureById(Long lectureId) {
        ExpertLectureDO lectureDO = expertLectureMapper.selectById(lectureId);
        if (lectureDO == null || lectureDO.getIsDelete() == 1) {
            return null;
        }
        ExpertLectureDTO dto = new ExpertLectureDTO();
        BeanUtils.copyProperties(lectureDO, dto);
        return dto;
    }

    @Override
    public IPage<ExpertLectureDTO> listLectures(long page, long size) {
        Page<ExpertLectureDO> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ExpertLectureDO> wrapper = new LambdaQueryWrapper<ExpertLectureDO>()
                .eq(ExpertLectureDO::getIsDelete, 0)
                .orderByDesc(ExpertLectureDO::getCreateTime)
                .orderByDesc(ExpertLectureDO::getId);

        IPage<ExpertLectureDO> pageResult = expertLectureMapper.selectPage(pageParam, wrapper);
        UserDTO currentUser = resolveCurrentLoginUser();

        return pageResult.convert(lectureDO -> {
            ExpertLectureDTO dto = new ExpertLectureDTO();
            BeanUtils.copyProperties(lectureDO, dto);
            dto.setSignedUp(lectureSignUpService.isSignedUp(currentUser, lectureDO == null ? null : lectureDO.getId()));
            return dto;
        });
    }

    @Override
    public IPage<ExpertLectureDTO> listLecturesByExpert(Long expertId, long page, long size) {
        Page<ExpertLectureDO> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ExpertLectureDO> wrapper = new LambdaQueryWrapper<ExpertLectureDO>()
                .eq(ExpertLectureDO::getExpertId, expertId)
                .eq(ExpertLectureDO::getIsDelete, 0)
                .orderByDesc(ExpertLectureDO::getCreateTime)
                .orderByDesc(ExpertLectureDO::getId);

        IPage<ExpertLectureDO> pageResult = expertLectureMapper.selectPage(pageParam, wrapper);

        return pageResult.convert(lectureDO -> {
            ExpertLectureDTO dto = new ExpertLectureDTO();
            BeanUtils.copyProperties(lectureDO, dto);
            return dto;
        });
    }

    @Override
    public boolean updateLectureStatus(UserDTO expertUser, Long lectureId, Integer status) {
        if (expertUser == null || expertUser.getId() == null) {
            throw new RuntimeException("用户未登录");
        }

        ExpertLectureDO existing = expertLectureMapper.selectById(lectureId);
        if (existing == null) {
            throw new RuntimeException("讲座不存在");
        }
        if (!existing.getExpertId().equals(expertUser.getId())) {
            throw new RuntimeException("无权修改他人的讲座状态");
        }

        existing.setStatus(status);
        existing.setUpdateTime(LocalDateTime.now());
        return expertLectureMapper.updateById(existing) > 0;
    }

    private UserDTO resolveCurrentLoginUser() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes servletRequestAttributes)) {
            return null;
        }
        Object loginUser = servletRequestAttributes.getRequest().getAttribute("loginUser");
        return loginUser instanceof UserDTO userDTO ? userDTO : null;
    }
}