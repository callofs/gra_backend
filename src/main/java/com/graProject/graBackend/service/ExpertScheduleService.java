package com.graProject.graBackend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graProject.graBackend.dto.ExpertScheduleDTO;
import com.graProject.graBackend.dto.UserDTO;

public interface ExpertScheduleService {

    ExpertScheduleDTO createSchedule(UserDTO loginUser, ExpertScheduleDTO scheduleDTO);

    boolean updateSchedule(UserDTO loginUser, ExpertScheduleDTO scheduleDTO);

    boolean deleteSchedule(UserDTO loginUser, Long scheduleId);

    IPage<ExpertScheduleDTO> listMySchedules(UserDTO loginUser, long page, long size);
}
