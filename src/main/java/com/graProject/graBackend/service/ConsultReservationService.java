package com.graProject.graBackend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graProject.graBackend.dto.ConsultReservationDTO;
import com.graProject.graBackend.dto.ExpertScheduleDTO;
import com.graProject.graBackend.dto.UserDTO;

import java.util.List;

public interface ConsultReservationService {

    List<ExpertScheduleDTO> listExpertSchedules(Long expertId);

    ConsultReservationDTO createReservation(UserDTO loginUser, ConsultReservationDTO reservationDTO);

    boolean cancelReservation(UserDTO loginUser, Long reservationId);

    IPage<ConsultReservationDTO> listMyReservations(UserDTO loginUser, long page, long size);

    IPage<ConsultReservationDTO> listExpertReservations(UserDTO loginUser, long page, long size);

    boolean updateReservationStatus(UserDTO loginUser, Long reservationId, Integer status);
}
