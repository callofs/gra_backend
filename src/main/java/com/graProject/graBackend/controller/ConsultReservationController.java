package com.graProject.graBackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graProject.graBackend.common.result.HttpCode;
import com.graProject.graBackend.common.result.HttpResult;
import com.graProject.graBackend.dto.ConsultReservationDTO;
import com.graProject.graBackend.dto.ExpertScheduleDTO;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.service.ConsultReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "专家咨询预约接口")
@RequestMapping("/api/consultReservation")
public class ConsultReservationController {

    private final ConsultReservationService consultReservationService;

    public ConsultReservationController(ConsultReservationService consultReservationService) {
        this.consultReservationService = consultReservationService;
    }

    @Operation(summary = "获取专家可预约排班")
    @GetMapping("/schedules/{expertId}")
    public HttpResult<List<ExpertScheduleDTO>> listExpertSchedules(@PathVariable("expertId") Long expertId) {
        return HttpResult.success(consultReservationService.listExpertSchedules(expertId));
    }

    @Operation(summary = "创建咨询预约")
    @PostMapping("/create")
    public HttpResult<ConsultReservationDTO> createReservation(HttpServletRequest request,
            @RequestBody ConsultReservationDTO reservationDTO) {
        Object loginUser = request.getAttribute("loginUser");
        UserDTO currentUser = loginUser instanceof UserDTO userDTO ? userDTO : null;
        if (currentUser == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        try {
            return HttpResult.success(consultReservationService.createReservation(currentUser, reservationDTO));
        } catch (RuntimeException e) {
            return HttpResult.fail(e.getMessage());
        }
    }

    @Operation(summary = "取消咨询预约")
    @PostMapping("/cancel/{reservationId}")
    public HttpResult<String> cancelReservation(HttpServletRequest request,
            @PathVariable("reservationId") Long reservationId) {
        Object loginUser = request.getAttribute("loginUser");
        UserDTO currentUser = loginUser instanceof UserDTO userDTO ? userDTO : null;
        if (currentUser == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        try {
            boolean success = consultReservationService.cancelReservation(currentUser, reservationId);
            return success ? HttpResult.success("取消预约成功") : HttpResult.fail("取消预约失败");
        } catch (RuntimeException e) {
            return HttpResult.fail(e.getMessage());
        }
    }

    @Operation(summary = "获取我的咨询预约列表")
    @GetMapping("/my")
    public HttpResult<IPage<ConsultReservationDTO>> listMyReservations(HttpServletRequest request,
            @RequestParam("page") long page,
            @RequestParam("size") long size) {
        Object loginUser = request.getAttribute("loginUser");
        UserDTO currentUser = loginUser instanceof UserDTO userDTO ? userDTO : null;
        if (currentUser == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        try {
            return HttpResult.success(consultReservationService.listMyReservations(currentUser, page, size));
        } catch (RuntimeException e) {
            return HttpResult.fail(e.getMessage());
        }
    }

    @Operation(summary = "专家查看收到的咨询预约列表")
    @GetMapping("/expert/list")
    public HttpResult<IPage<ConsultReservationDTO>> listExpertReservations(HttpServletRequest request,
            @RequestParam("page") long page,
            @RequestParam("size") long size) {
        Object loginUser = request.getAttribute("loginUser");
        UserDTO currentUser = loginUser instanceof UserDTO userDTO ? userDTO : null;
        if (currentUser == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        try {
            return HttpResult.success(consultReservationService.listExpertReservations(currentUser, page, size));
        } catch (RuntimeException e) {
            return HttpResult.fail(e.getMessage());
        }
    }

    @Operation(summary = "专家处理咨询预约状态")
    @PutMapping("/status/{reservationId}")
    public HttpResult<String> updateReservationStatus(HttpServletRequest request,
            @PathVariable("reservationId") Long reservationId,
            @RequestParam("status") Integer status) {
        Object loginUser = request.getAttribute("loginUser");
        UserDTO currentUser = loginUser instanceof UserDTO userDTO ? userDTO : null;
        if (currentUser == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        try {
            boolean success = consultReservationService.updateReservationStatus(currentUser, reservationId, status);
            return success ? HttpResult.success("处理成功") : HttpResult.fail("处理失败");
        } catch (RuntimeException e) {
            return HttpResult.fail(e.getMessage());
        }
    }
}
