package com.graProject.graBackend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graProject.graBackend.dto.LectureSignUpDTO;
import com.graProject.graBackend.dto.UserDTO;

public interface LectureSignUpService {

    LectureSignUpDTO signUp(UserDTO user, Long lectureId);

    boolean cancelSignUp(UserDTO user, Long lectureId);

    boolean isSignedUp(UserDTO user, Long lectureId);

    IPage<LectureSignUpDTO> listMySignUps(UserDTO user, long page, long size);

    IPage<LectureSignUpDTO> listSignUpsByLecture(Long lectureId, long page, long size);

    boolean updateAttendance(Long signUpId, Integer isAttend);
}