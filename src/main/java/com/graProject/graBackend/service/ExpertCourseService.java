package com.graProject.graBackend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graProject.graBackend.dto.ExpertCourseDTO;
import com.graProject.graBackend.dto.UserDTO;

public interface ExpertCourseService {

    String uploadCourseFile(UserDTO expertUser, String originalFilename, byte[] bytes);

    String uploadCourseCover(UserDTO expertUser, String originalFilename, byte[] bytes);

    ExpertCourseDTO createCourse(UserDTO expertUser, ExpertCourseDTO courseDTO);

    boolean updateCourse(UserDTO expertUser, ExpertCourseDTO courseDTO);

    boolean deleteCourse(UserDTO expertUser, Long courseId);

    ExpertCourseDTO getCourseDetail(UserDTO loginUser, Long courseId);

    IPage<ExpertCourseDTO> listApprovedCourses(long page, long size);

    IPage<ExpertCourseDTO> listMyCourses(UserDTO expertUser, long page, long size);

    IPage<ExpertCourseDTO> listAllCourses(long page, long size, Integer auditStatus, Long expertId, String keyword);

    boolean auditCourse(UserDTO adminUser, Long courseId, Integer auditStatus, String auditComment);
}
