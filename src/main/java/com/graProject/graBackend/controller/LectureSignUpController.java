package com.graProject.graBackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graProject.graBackend.common.result.HttpCode;
import com.graProject.graBackend.common.result.HttpResult;
import com.graProject.graBackend.dto.LectureSignUpDTO;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.service.LectureSignUpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "讲座报名接口")
@RequestMapping("/api/lecture/signup")
public class LectureSignUpController {

    private final LectureSignUpService lectureSignUpService;

    public LectureSignUpController(LectureSignUpService lectureSignUpService) {
        this.lectureSignUpService = lectureSignUpService;
    }

    @Operation(summary = "报名讲座")
    @PostMapping("/{lectureId}")
    public HttpResult<LectureSignUpDTO> signUp(
            HttpServletRequest request,
            @PathVariable("lectureId") Long lectureId) {
        Object loginUser = request.getAttribute("loginUser");
        UserDTO currentUser = loginUser instanceof UserDTO userDTO ? userDTO : null;
        if (currentUser == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        try {
            LectureSignUpDTO result = lectureSignUpService.signUp(currentUser, lectureId);
            return HttpResult.success(result);
        } catch (RuntimeException e) {
            return HttpResult.fail(e.getMessage());
        }
    }

    @Operation(summary = "取消报名")
    @PostMapping("/cancel/{lectureId}")
    public HttpResult<String> cancelSignUp(
            HttpServletRequest request,
            @PathVariable Long lectureId) {
        Object loginUser = request.getAttribute("loginUser");
        UserDTO currentUser = loginUser instanceof UserDTO userDTO ? userDTO : null;
        if (currentUser == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        try {
            boolean success = lectureSignUpService.cancelSignUp(currentUser, lectureId);
            return success ? HttpResult.success("取消报名成功") : HttpResult.fail("取消报名失败");
        } catch (RuntimeException e) {
            return HttpResult.fail(e.getMessage());
        }
    }

    @Operation(summary = "检查是否已报名")
    @GetMapping("/check/{lectureId}")
    public HttpResult<Boolean> isSignedUp(
            HttpServletRequest request,
            @PathVariable Long lectureId) {
        Object loginUser = request.getAttribute("loginUser");
        UserDTO currentUser = loginUser instanceof UserDTO userDTO ? userDTO : null;
        boolean result = lectureSignUpService.isSignedUp(currentUser, lectureId);
        return HttpResult.success(result);
    }

    @Operation(summary = "获取我的报名列表")
    @GetMapping("/my")
    public HttpResult<IPage<LectureSignUpDTO>> listMySignUps(
            HttpServletRequest request,
            @RequestParam long page,
            @RequestParam long size) {
        Object loginUser = request.getAttribute("loginUser");
        UserDTO currentUser = loginUser instanceof UserDTO userDTO ? userDTO : null;
        if (currentUser == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        IPage<LectureSignUpDTO> result = lectureSignUpService.listMySignUps(currentUser, page, size);
        return HttpResult.success(result);
    }

    @Operation(summary = "获取讲座的报名列表")
    @GetMapping("/lecture/{lectureId}")
    public HttpResult<IPage<LectureSignUpDTO>> listSignUpsByLecture(
            @PathVariable Long lectureId,
            @RequestParam long page,
            @RequestParam long size) {
        IPage<LectureSignUpDTO> result = lectureSignUpService.listSignUpsByLecture(lectureId, page, size);
        return HttpResult.success(result);
    }

    @Operation(summary = "更新到场状态")
    @PutMapping("/attendance/{signUpId}")
    public HttpResult<String> updateAttendance(
            @PathVariable Long signUpId,
            @RequestParam Integer isAttend) {
        try {
            boolean success = lectureSignUpService.updateAttendance(signUpId, isAttend);
            return success ? HttpResult.success("状态更新成功") : HttpResult.fail("状态更新失败");
        } catch (RuntimeException e) {
            return HttpResult.fail(e.getMessage());
        }
    }
}