package com.graProject.graBackend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graProject.graBackend.dto.ExpertLectureDTO;
import com.graProject.graBackend.dto.UserDTO;

public interface ExpertLectureService {

    ExpertLectureDTO createLecture(UserDTO expertUser, ExpertLectureDTO lectureDTO);

    boolean updateLecture(UserDTO expertUser, ExpertLectureDTO lectureDTO);

    boolean deleteLecture(UserDTO expertUser, Long lectureId);

    ExpertLectureDTO getLectureById(Long lectureId);

    IPage<ExpertLectureDTO> listLectures(long page, long size);

    IPage<ExpertLectureDTO> listLecturesByExpert(Long expertId, long page, long size);

    boolean updateLectureStatus(UserDTO expertUser, Long lectureId, Integer status);
}