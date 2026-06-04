package com.graProject.graBackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graProject.graBackend.common.exception.User.UserLoginException;
import com.graProject.graBackend.common.result.HttpCode;
import com.graProject.graBackend.common.utils.AliyunOssUtil;
import com.graProject.graBackend.common.utils.UploadFileUtil;
import com.graProject.graBackend.dto.ExpertCourseDTO;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.entity.ExpertCourseDO;
import com.graProject.graBackend.entity.UserDO;
import com.graProject.graBackend.mapper.ExpertCourseMapper;
import com.graProject.graBackend.mapper.UserMapper;
import com.graProject.graBackend.service.ExpertCourseService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class ExpertCourseServiceImpl implements ExpertCourseService {

    private static final Set<String> ALLOW_VIDEO_EXT = Set.of("mp4", "mov", "avi", "mkv", "flv", "wmv");

    private static final Set<String> IMAGE_EXT = Set.of("png", "jpg", "jpeg", "gif", "webp");

    private final ExpertCourseMapper expertCourseMapper;
    private final UserMapper userMapper;
    private final AliyunOssUtil aliyunOssUtil;

    public ExpertCourseServiceImpl(ExpertCourseMapper expertCourseMapper,
            UserMapper userMapper,
            AliyunOssUtil aliyunOssUtil) {
        this.expertCourseMapper = expertCourseMapper;
        this.userMapper = userMapper;
        this.aliyunOssUtil = aliyunOssUtil;
    }

    @Override
    public String uploadCourseFile(UserDTO expertUser, String originalFilename, byte[] bytes) {
        Long expertId = requireExpertUser(expertUser);
        if (bytes == null || bytes.length == 0) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "课程文件不能为空");
        }
        String ext = UploadFileUtil.getExtensionLower(originalFilename);
        if (!StringUtils.hasText(ext) || !ALLOW_VIDEO_EXT.contains(ext)) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "仅支持上传视频格式：" + ALLOW_VIDEO_EXT);
        }
        String dateFolder = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String objectKey = "expert_course/" + expertId + "/" + dateFolder + "/" + UUID.randomUUID() + "." + ext;
        aliyunOssUtil.putObject(objectKey, bytes);
        return objectKey;
    }

    @Override
    public String uploadCourseCover(UserDTO expertUser, String originalFilename, byte[] bytes) {
        Long expertId = requireExpertUser(expertUser);
        if (bytes == null || bytes.length == 0) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "封面文件不能为空");
        }
        String ext = UploadFileUtil.getExtensionLower(originalFilename);
        if (!StringUtils.hasText(ext) || !IMAGE_EXT.contains(ext)) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "封面仅支持图片格式：" + IMAGE_EXT);
        }
        String dateFolder = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String objectKey = "expert_course/cover/" + expertId + "/" + dateFolder + "/" + UUID.randomUUID() + "." + ext;
        aliyunOssUtil.putObject(objectKey, bytes);
        return objectKey;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExpertCourseDTO createCourse(UserDTO expertUser, ExpertCourseDTO courseDTO) {
        Long expertId = requireExpertUser(expertUser);
        validateCoursePayload(courseDTO);
        if (!StringUtils.hasText(courseDTO.getVideoObjectKey())) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "请先上传课程视频文件");
        }
        ExpertCourseDO courseDO = new ExpertCourseDO();
        BeanUtils.copyProperties(courseDTO, courseDO);
        courseDO.setId(null);
        courseDO.setExpertId(expertId);
        courseDO.setAuditStatus(0);
        courseDO.setAuditComment(null);
        courseDO.setAuditBy(null);
        courseDO.setAuditTime(null);
        courseDO.setViewCount(0);
        courseDO.setCreateTime(LocalDateTime.now());
        courseDO.setUpdateTime(LocalDateTime.now());
        courseDO.setIsDelete(0);
        expertCourseMapper.insert(courseDO);
        return buildCourseDTO(courseDO, expertUser.getNickname(), null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCourse(UserDTO expertUser, ExpertCourseDTO courseDTO) {
        Long expertId = requireExpertUser(expertUser);
        if (courseDTO == null || courseDTO.getId() == null) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "课程ID不能为空");
        }
        ExpertCourseDO existing = loadCourse(courseDTO.getId());
        if (!expertId.equals(existing.getExpertId())) {
            throw new UserLoginException(HttpCode.FORBIDDEN, "无法修改他人的课程");
        }
        if (courseDTO.getTitle() != null) {
            existing.setTitle(courseDTO.getTitle());
        }
        if (courseDTO.getCoverUrl() != null) {
            existing.setCoverUrl(courseDTO.getCoverUrl());
        }
        if (courseDTO.getCourseDesc() != null) {
            existing.setCourseDesc(courseDTO.getCourseDesc());
        }
        if (courseDTO.getVideoObjectKey() != null) {
            existing.setVideoObjectKey(courseDTO.getVideoObjectKey());
        }
        if (courseDTO.getDurationSeconds() != null) {
            existing.setDurationSeconds(courseDTO.getDurationSeconds());
        }
        existing.setAuditStatus(0);
        existing.setAuditComment(null);
        existing.setAuditBy(null);
        existing.setAuditTime(null);
        existing.setUpdateTime(LocalDateTime.now());
        return expertCourseMapper.updateById(existing) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCourse(UserDTO expertUser, Long courseId) {
        Long expertId = requireExpertUser(expertUser);
        ExpertCourseDO existing = loadCourse(courseId);
        if (!expertId.equals(existing.getExpertId())) {
            throw new UserLoginException(HttpCode.FORBIDDEN, "无法删除他人的课程");
        }
        existing.setIsDelete(1);
        existing.setUpdateTime(LocalDateTime.now());
        return expertCourseMapper.updateById(existing) > 0;
    }

    @Override
    public ExpertCourseDTO getCourseDetail(UserDTO loginUser, Long courseId) {
        ExpertCourseDO courseDO = loadCourse(courseId);
        boolean isOwner = loginUser != null && loginUser.getId() != null
                && loginUser.getId().equals(courseDO.getExpertId());
        boolean isAdmin = loginUser != null && loginUser.getRole() != null && loginUser.getRole() == 3;
        if (courseDO.getAuditStatus() == null || courseDO.getAuditStatus() != 1) {
            if (!isOwner && !isAdmin) {
                throw new UserLoginException(HttpCode.FORBIDDEN, "课程未通过审核");
            }
        }
        if (!isOwner && courseDO.getAuditStatus() != null && courseDO.getAuditStatus() == 1) {
            increaseViewCount(courseDO.getId());
            courseDO.setViewCount(courseDO.getViewCount() == null ? 1 : courseDO.getViewCount() + 1);
        }
        UserDO expert = userMapper.selectById(courseDO.getExpertId());
        return buildCourseDTO(courseDO, expert == null ? null : expert.getNickname(),
                expert == null ? null : expert.getAvatar());
    }

    @Override
    public IPage<ExpertCourseDTO> listApprovedCourses(long page, long size) {
        Page<ExpertCourseDO> pageParam = new Page<>(Math.max(1, page), Math.min(Math.max(1, size), 50));
        LambdaQueryWrapper<ExpertCourseDO> wrapper = new LambdaQueryWrapper<ExpertCourseDO>()
                .eq(ExpertCourseDO::getIsDelete, 0)
                .eq(ExpertCourseDO::getAuditStatus, 1)
                .orderByDesc(ExpertCourseDO::getCreateTime)
                .orderByDesc(ExpertCourseDO::getId);
        IPage<ExpertCourseDO> pageResult = expertCourseMapper.selectPage(pageParam, wrapper);
        Map<Long, UserDO> expertMap = resolveExpertMap(pageResult.getRecords());
        return pageResult.convert(courseDO -> buildCourseDTO(courseDO,
                getNickname(expertMap, courseDO.getExpertId()),
                getAvatar(expertMap, courseDO.getExpertId())));
    }

    @Override
    public IPage<ExpertCourseDTO> listMyCourses(UserDTO expertUser, long page, long size) {
        Long expertId = requireExpertUser(expertUser);
        Page<ExpertCourseDO> pageParam = new Page<>(Math.max(1, page), Math.min(Math.max(1, size), 50));
        LambdaQueryWrapper<ExpertCourseDO> wrapper = new LambdaQueryWrapper<ExpertCourseDO>()
                .eq(ExpertCourseDO::getExpertId, expertId)
                .eq(ExpertCourseDO::getIsDelete, 0)
                .orderByDesc(ExpertCourseDO::getCreateTime)
                .orderByDesc(ExpertCourseDO::getId);
        IPage<ExpertCourseDO> pageResult = expertCourseMapper.selectPage(pageParam, wrapper);
        return pageResult.convert(courseDO -> buildCourseDTO(courseDO, expertUser.getNickname(), null));
    }

    @Override
    public IPage<ExpertCourseDTO> listAllCourses(long page, long size, Integer auditStatus, Long expertId,
            String keyword) {
        Page<ExpertCourseDO> pageParam = new Page<>(Math.max(1, page), Math.min(Math.max(1, size), 50));
        LambdaQueryWrapper<ExpertCourseDO> wrapper = new LambdaQueryWrapper<ExpertCourseDO>()
                .eq(ExpertCourseDO::getIsDelete, 0);
        if (auditStatus != null) {
            wrapper.eq(ExpertCourseDO::getAuditStatus, auditStatus);
        }
        if (expertId != null) {
            wrapper.eq(ExpertCourseDO::getExpertId, expertId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(ExpertCourseDO::getTitle, keyword);
        }
        wrapper.orderByDesc(ExpertCourseDO::getCreateTime)
                .orderByDesc(ExpertCourseDO::getId);
        IPage<ExpertCourseDO> pageResult = expertCourseMapper.selectPage(pageParam, wrapper);
        Map<Long, UserDO> expertMap = resolveExpertMap(pageResult.getRecords());
        return pageResult.convert(courseDO -> buildCourseDTO(courseDO,
                getNickname(expertMap, courseDO.getExpertId()),
                getAvatar(expertMap, courseDO.getExpertId())));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditCourse(UserDTO adminUser, Long courseId, Integer auditStatus, String auditComment) {
        Long adminId = requireAdminUser(adminUser);
        if (auditStatus == null || (auditStatus != 1 && auditStatus != 2 && auditStatus != 3)) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "审核状态不合法");
        }
        ExpertCourseDO courseDO = loadCourse(courseId);
        courseDO.setAuditStatus(auditStatus);
        courseDO.setAuditComment(auditComment);
        courseDO.setAuditBy(adminId);
        courseDO.setAuditTime(LocalDateTime.now());
        courseDO.setUpdateTime(LocalDateTime.now());
        return expertCourseMapper.updateById(courseDO) > 0;
    }

    private void increaseViewCount(Long courseId) {
        if (courseId == null) {
            return;
        }
        expertCourseMapper.update(null, new LambdaUpdateWrapper<ExpertCourseDO>()
                .eq(ExpertCourseDO::getId, courseId)
                .setSql("view_count = COALESCE(view_count,0) + 1"));
    }

    private ExpertCourseDO loadCourse(Long courseId) {
        if (courseId == null) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "课程ID不能为空");
        }
        ExpertCourseDO courseDO = expertCourseMapper.selectById(courseId);
        if (courseDO == null || courseDO.getIsDelete() != null && courseDO.getIsDelete() == 1) {
            throw new UserLoginException(HttpCode.NOT_FOUND, "课程不存在");
        }
        return courseDO;
    }

    private void validateCoursePayload(ExpertCourseDTO courseDTO) {
        if (courseDTO == null || !StringUtils.hasText(courseDTO.getTitle())) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "课程标题不能为空");
        }
        if (!StringUtils.hasText(courseDTO.getCourseDesc())) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "课程简介不能为空");
        }
        if (!StringUtils.hasText(courseDTO.getCoverUrl())) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "课程封面不能为空");
        }
    }

    private Long requireExpertUser(UserDTO user) {
        if (user == null || user.getId() == null) {
            throw new UserLoginException(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        if (user.getRole() == null || user.getRole() != 2) {
            throw new UserLoginException(HttpCode.FORBIDDEN, "仅专家可进行此操作");
        }
        return user.getId();
    }

    private Long requireAdminUser(UserDTO user) {
        if (user == null || user.getId() == null) {
            throw new UserLoginException(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        if (user.getRole() == null || user.getRole() != 3) {
            throw new UserLoginException(HttpCode.FORBIDDEN, "仅管理员可审核课程");
        }
        return user.getId();
    }

    private ExpertCourseDTO buildCourseDTO(ExpertCourseDO courseDO, String nickname, byte[] avatar) {
        if (courseDO == null) {
            return null;
        }
        ExpertCourseDTO dto = new ExpertCourseDTO();
        BeanUtils.copyProperties(courseDO, dto);
        dto.setExpertNickname(nickname);
        dto.setExpertAvatar(avatar);
        return dto;
    }

    private Map<Long, UserDO> resolveExpertMap(java.util.List<ExpertCourseDO> courses) {
        if (courses == null || courses.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        java.util.Set<Long> expertIds = courses.stream()
                .filter(course -> course != null && course.getExpertId() != null)
                .map(ExpertCourseDO::getExpertId)
                .collect(java.util.stream.Collectors.toSet());
        if (expertIds.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<UserDO>()
                .in(UserDO::getId, expertIds)
                .eq(UserDO::getIsDelete, 0);
        java.util.List<UserDO> users = userMapper.selectList(wrapper);
        if (users == null || users.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        return users.stream()
                .filter(user -> user != null && user.getId() != null)
                .collect(java.util.stream.Collectors.toMap(UserDO::getId, user -> user, (left, right) -> left));
    }

    private String getNickname(Map<Long, UserDO> map, Long expertId) {
        if (map == null || expertId == null) {
            return null;
        }
        UserDO userDO = map.get(expertId);
        return userDO == null ? null : userDO.getNickname();
    }

    private byte[] getAvatar(Map<Long, UserDO> map, Long expertId) {
        if (map == null || expertId == null) {
            return null;
        }
        UserDO userDO = map.get(expertId);
        return userDO == null ? null : userDO.getAvatar();
    }
}
