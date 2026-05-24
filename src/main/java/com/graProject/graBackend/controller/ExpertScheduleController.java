package com.graProject.graBackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graProject.graBackend.common.result.HttpCode;
import com.graProject.graBackend.common.result.HttpResult;
import com.graProject.graBackend.dto.ExpertScheduleDTO;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.service.ExpertScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "专家排班管理接口")
@RequestMapping("/api/expertSchedule")
public class ExpertScheduleController {

    private final ExpertScheduleService expertScheduleService;

    public ExpertScheduleController(ExpertScheduleService expertScheduleService) {
        this.expertScheduleService = expertScheduleService;
    }

    @Operation(summary = "新增排班")
    @PostMapping("/create")
    public HttpResult<ExpertScheduleDTO> createSchedule(HttpServletRequest request,
            @RequestBody ExpertScheduleDTO scheduleDTO) {
        Object loginUser = request.getAttribute("loginUser");
        UserDTO currentUser = loginUser instanceof UserDTO userDTO ? userDTO : null;
        if (currentUser == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        try {
            return HttpResult.success(expertScheduleService.createSchedule(currentUser, scheduleDTO));
        } catch (RuntimeException e) {
            return HttpResult.fail(e.getMessage());
        }
    }

    @Operation(summary = "修改排班")
    @PutMapping("/update")
    public HttpResult<String> updateSchedule(HttpServletRequest request,
            @RequestBody ExpertScheduleDTO scheduleDTO) {
        Object loginUser = request.getAttribute("loginUser");
        UserDTO currentUser = loginUser instanceof UserDTO userDTO ? userDTO : null;
        if (currentUser == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        try {
            boolean success = expertScheduleService.updateSchedule(currentUser, scheduleDTO);
            return success ? HttpResult.success("修改成功") : HttpResult.fail("修改失败");
        } catch (RuntimeException e) {
            return HttpResult.fail(e.getMessage());
        }
    }

    @Operation(summary = "删除排班")
    @PostMapping("/delete/{scheduleId}")
    public HttpResult<String> deleteSchedule(HttpServletRequest request,
            @PathVariable("scheduleId") Long scheduleId) {
        Object loginUser = request.getAttribute("loginUser");
        UserDTO currentUser = loginUser instanceof UserDTO userDTO ? userDTO : null;
        if (currentUser == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        try {
            boolean success = expertScheduleService.deleteSchedule(currentUser, scheduleId);
            return success ? HttpResult.success("删除成功") : HttpResult.fail("删除失败");
        } catch (RuntimeException e) {
            return HttpResult.fail(e.getMessage());
        }
    }

    @Operation(summary = "获取我的排班列表")
    @GetMapping("/my")
    public HttpResult<IPage<ExpertScheduleDTO>> listMySchedules(HttpServletRequest request,
            @RequestParam("page") long page,
            @RequestParam("size") long size) {
        Object loginUser = request.getAttribute("loginUser");
        UserDTO currentUser = loginUser instanceof UserDTO userDTO ? userDTO : null;
        if (currentUser == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        try {
            return HttpResult.success(expertScheduleService.listMySchedules(currentUser, page, size));
        } catch (RuntimeException e) {
            return HttpResult.fail(e.getMessage());
        }
    }
}
