package com.graProject.graBackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graProject.graBackend.dto.LectureSignUpDTO;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.entity.ExpertLectureDO;
import com.graProject.graBackend.entity.LectureSignUpDO;
import com.graProject.graBackend.mapper.ExpertLectureMapper;
import com.graProject.graBackend.mapper.LectureSignUpMapper;
import com.graProject.graBackend.service.LectureSignUpService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LectureSignUpServiceImpl implements LectureSignUpService {

    private final LectureSignUpMapper lectureSignUpMapper;
    private final ExpertLectureMapper expertLectureMapper;

    public LectureSignUpServiceImpl(LectureSignUpMapper lectureSignUpMapper, ExpertLectureMapper expertLectureMapper) {
        this.lectureSignUpMapper = lectureSignUpMapper;
        this.expertLectureMapper = expertLectureMapper;
    }

    @Override
    @Transactional
    public LectureSignUpDTO signUp(UserDTO user, Long lectureId) {
        if (user == null || user.getId() == null) {
            throw new RuntimeException("用户未登录");
        }

        ExpertLectureDO lecture = expertLectureMapper.selectById(lectureId);
        if (lecture == null || lecture.getIsDelete() == 1) {
            throw new RuntimeException("讲座不存在");
        }

        LambdaQueryWrapper<LectureSignUpDO> wrapper = new LambdaQueryWrapper<LectureSignUpDO>()
                .eq(LectureSignUpDO::getUserId, user.getId())
                .eq(LectureSignUpDO::getLectureId, lectureId);
        LectureSignUpDO existing = lectureSignUpMapper.selectOne(wrapper);
        if (existing != null) {
            throw new RuntimeException("已成功报名过该讲座");
        }

        Integer maxSignUp = lecture.getMaxSignUp();
        if (maxSignUp != null && maxSignUp > 0 && lecture.getSignUpCount() >= maxSignUp) {
            throw new RuntimeException("报名人数已满");
        }

        LectureSignUpDO signUpDO = new LectureSignUpDO();
        signUpDO.setLectureId(lectureId);
        signUpDO.setUserId(user.getId());
        signUpDO.setIsAttend(0);
        signUpDO.setCreateTime(LocalDateTime.now());

        lectureSignUpMapper.insert(signUpDO);

        lecture.setSignUpCount(lecture.getSignUpCount() + 1);
        expertLectureMapper.updateById(lecture);

        LectureSignUpDTO dto = new LectureSignUpDTO();
        BeanUtils.copyProperties(signUpDO, dto);
        return dto;
    }

    @Override
    @Transactional
    public boolean cancelSignUp(UserDTO user, Long lectureId) {
        if (user == null || user.getId() == null) {
            throw new RuntimeException("用户未登录");
        }

        LambdaQueryWrapper<LectureSignUpDO> wrapper = new LambdaQueryWrapper<LectureSignUpDO>()
                .eq(LectureSignUpDO::getUserId, user.getId())
                .eq(LectureSignUpDO::getLectureId, lectureId);
        LectureSignUpDO existing = lectureSignUpMapper.selectOne(wrapper);
        if (existing == null) {
            throw new RuntimeException("未找到报名记录");
        }

        int result = lectureSignUpMapper.delete(wrapper);

        if (result > 0) {
            ExpertLectureDO lecture = expertLectureMapper.selectById(lectureId);
            if (lecture != null && lecture.getSignUpCount() > 0) {
                lecture.setSignUpCount(lecture.getSignUpCount() - 1);
                expertLectureMapper.updateById(lecture);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isSignedUp(UserDTO user, Long lectureId) {
        if (user == null || user.getId() == null) {
            return false;
        }
        LambdaQueryWrapper<LectureSignUpDO> wrapper = new LambdaQueryWrapper<LectureSignUpDO>()
                .eq(LectureSignUpDO::getUserId, user.getId())
                .eq(LectureSignUpDO::getLectureId, lectureId);
        return lectureSignUpMapper.selectCount(wrapper) > 0;
    }

    @Override
    public IPage<LectureSignUpDTO> listMySignUps(UserDTO user, long page, long size) {
        if (user == null || user.getId() == null) {
            throw new RuntimeException("用户未登录");
        }
        Page<LectureSignUpDO> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<LectureSignUpDO> wrapper = new LambdaQueryWrapper<LectureSignUpDO>()
                .eq(LectureSignUpDO::getUserId, user.getId())
                .orderByDesc(LectureSignUpDO::getCreateTime);

        IPage<LectureSignUpDO> pageResult = lectureSignUpMapper.selectPage(pageParam, wrapper);
        return pageResult.convert(signUpDO -> {
            LectureSignUpDTO dto = new LectureSignUpDTO();
            BeanUtils.copyProperties(signUpDO, dto);
            return dto;
        });
    }

    @Override
    public IPage<LectureSignUpDTO> listSignUpsByLecture(Long lectureId, long page, long size) {
        Page<LectureSignUpDO> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<LectureSignUpDO> wrapper = new LambdaQueryWrapper<LectureSignUpDO>()
                .eq(LectureSignUpDO::getLectureId, lectureId)
                .orderByDesc(LectureSignUpDO::getCreateTime);

        IPage<LectureSignUpDO> pageResult = lectureSignUpMapper.selectPage(pageParam, wrapper);
        return pageResult.convert(signUpDO -> {
            LectureSignUpDTO dto = new LectureSignUpDTO();
            BeanUtils.copyProperties(signUpDO, dto);
            return dto;
        });
    }

    @Override
    public boolean updateAttendance(Long signUpId, Integer isAttend) {
        LectureSignUpDO signUp = lectureSignUpMapper.selectById(signUpId);
        if (signUp == null) {
            throw new RuntimeException("报名记录不存在");
        }
        signUp.setIsAttend(isAttend);
        return lectureSignUpMapper.updateById(signUp) > 0;
    }
}