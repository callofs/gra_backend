package com.graProject.graBackend.controller;

import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员用户接口。
 */
@RestController
@Tag(name = "管理员用户接口")
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取指定用户的专家认证材料（管理员）。
     *
     * @param userId  被查看的用户 ID
     * @param request 当前请求
     * @return 认证材料二进制流响应
     */
    @Operation(summary = "管理员获取指定用户认证材料")
    @GetMapping(value = "/{userId}/certificationMaterial", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getUserCertificationMaterial(@PathVariable("userId") Long userId,
            HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        byte[] bytes = userService.getUserCertificationMaterialByAdmin(userDTO, userId);
        if (bytes == null || bytes.length == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .header("X-Message", "User has not uploaded certification material")
                    .build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .contentLength(bytes.length)
                .body(bytes);
    }
}
