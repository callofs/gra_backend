package com.graProject.graBackend.controller;

import com.graProject.graBackend.common.result.HttpCode;
import com.graProject.graBackend.common.result.HttpResult;
import com.graProject.graBackend.dto.LoginRequestDTO;
import com.graProject.graBackend.dto.LoginResponseDTO;
import com.graProject.graBackend.dto.RegisterRequestDto;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

/**
 * 用户接口。
 */

@RestController
@Tag(name = "用户接口测试")
@RequestMapping("/api/user")
public class UserController {

    /**
     * 用户服务。
     */
    private final UserService userService;

    private final ObjectMapper objectMapper;

    /**
     * JWT Cookie 名称。
     */
    private final String jwtCookieName;

    /**
     * JWT Cookie 路径。
     */
    private final String jwtCookiePath;

    /**
     * JWT Cookie 是否仅允许 HTTPS 传输。
     */
    private final boolean jwtCookieSecure;

    /**
     * JWT Cookie SameSite 策略。
     */
    private final String jwtCookieSameSite;

    /**
     * 构造方法。
     *
     * @param userService       用户服务
     * @param jwtCookieName     JWT Cookie 名称
     * @param jwtCookiePath     JWT Cookie 路径
     * @param jwtCookieSecure   JWT Cookie 是否仅允许 HTTPS 传输
     * @param jwtCookieSameSite JWT Cookie SameSite 策略
     */
    public UserController(UserService userService,
            ObjectMapper objectMapper,
            @Value("${jwt.cookie.name:gra_token}") String jwtCookieName,
            @Value("${jwt.cookie.path:/}") String jwtCookiePath,
            @Value("${jwt.cookie.secure:false}") boolean jwtCookieSecure,
            @Value("${jwt.cookie.same-site:Lax}") String jwtCookieSameSite) {
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.jwtCookieName = jwtCookieName;
        this.jwtCookiePath = jwtCookiePath;
        this.jwtCookieSecure = jwtCookieSecure;
        this.jwtCookieSameSite = jwtCookieSameSite;
    }

    /**
     * 登录。
     *
     * @param loginRequestDTO 登录请求
     * @param response        当前响应
     * @return 登录结果
     */
    @Operation(summary = "登录接口")
    @PostMapping("/login")
    public HttpResult<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO,
            HttpServletResponse response) {
        LoginResponseDTO loginResponseDTO = userService.login(loginRequestDTO);
        writeTokenCookie(loginResponseDTO, response);
        LoginResponseDTO responseDTO = LoginResponseDTO.builder()
                .expiresIn(loginResponseDTO.getExpiresIn())
                .userInfo(loginResponseDTO.getUserInfo())
                .build();
        return HttpResult.success(responseDTO);
    }

    /**
     * 用户注册。
     * 调用用户服务完成用户名校验、密码加密和持久化，注册成功后返回注册结果。
     *
     * @param registerRequestDto 注册请求
     * @param certificationFile  专家认证材料文件（专家角色必传）
     * @return 注册结果
     */
    @Operation(summary = "注册接口")
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public HttpResult<String> register(@ModelAttribute RegisterRequestDto registerRequestDto,
            @RequestPart(value = "registerRequestDto", required = false) String registerRequestDtoJson,
            @RequestPart(value = "certificationFile", required = false) MultipartFile certificationFile) {
        if (registerRequestDto == null
                || (registerRequestDto.getUsername() == null
                        && registerRequestDto.getPassword() == null
                        && registerRequestDto.getNickname() == null
                        && registerRequestDto.getPhone() == null
                        && registerRequestDto.getRole() == null
                        && registerRequestDto.getEmail() == null)) {
            if (registerRequestDtoJson != null && !registerRequestDtoJson.isBlank()) {
                try {
                    registerRequestDto = objectMapper.readValue(registerRequestDtoJson, RegisterRequestDto.class);
                } catch (Exception e) {
                    return HttpResult.of(HttpCode.BAD_REQUEST, "注册参数格式不正确");
                }
            }
        }
        return HttpResult.success(userService.register(registerRequestDto, certificationFile));
    }

    /**
     * 获取当前登录用户。
     * 从 JWT 拦截器写入的请求上下文中读取当前登录用户标识，
     * 再从数据库查询并返回当前用户完整资料。
     *
     * @param request 当前请求
     * @return 当前登录用户信息
     */
    @Operation(summary = "获取当前登录用户")
    @GetMapping("/profile")
    public HttpResult<UserDTO> getCurrentUser(HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO)) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        return HttpResult.success(userService.getCurrentUserProfile(userDTO));
    }

    /**
     * 修改当前用户头像。
     * 接收图片文件，并将其二进制内容更新到当前用户的 avatar 字段。
     *
     * @param file    头像图片文件
     * @param request 当前请求
     * @return 修改结果
     */
    @Operation(summary = "修改头像")
    @PostMapping(value = "/updateAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public HttpResult<String> updateAvatar(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO)) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        return HttpResult.success(userService.updateAvatar(userDTO, file));
    }

    /**
     * 获取当前用户头像。
     * 从当前登录用户对应的数据库记录中读取 avatar 二进制数据，
     * 并以图片二进制流的形式直接输出。
     *
     * @param request 当前请求
     * @return 当前用户头像二进制流响应
     */
    @Operation(summary = "获取当前用户头像")
    @GetMapping(value = "/getAvatar", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getCurrentUserAvatar(HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        byte[] avatar = userService.getCurrentUserAvatar(userDTO);
        if (avatar == null || avatar.length == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .header("X-Message", "No avatar found")
                    .build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .contentLength(avatar.length)
                .body(avatar);
    }

    /**
     * 获取当前登录用户的专家认证材料。
     *
     * @param request 当前请求
     * @return 认证材料二进制流响应
     */
    @Operation(summary = "获取当前用户认证材料")
    @GetMapping(value = "/certificationMaterial", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getCurrentUserCertificationMaterial(HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        byte[] bytes = userService.getCurrentUserCertificationMaterial(userDTO);
        if (bytes == null || bytes.length == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .header("X-Message", "用户未上传认证材料")
                    .build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .contentLength(bytes.length)
                .body(bytes);
    }

    /**
     * 登出。
     * 通过覆盖并立即过期 JWT Cookie 的方式清除当前登录态。
     *
     * @param response 当前响应
     * @return 登出结果
     */
    @Operation(summary = "登出接口")
    @PostMapping("/logout")
    public HttpResult<String> logout(HttpServletResponse response) {
        clearTokenCookie(response);
        return HttpResult.success("退出登录成功");
    }

    /**
     * 将 JWT 写入 HttpOnly Cookie。
     *
     * @param loginResponseDTO 登录响应对象
     * @param response         当前响应
     */
    private void writeTokenCookie(LoginResponseDTO loginResponseDTO, HttpServletResponse response) {
        String token = Objects.requireNonNull(loginResponseDTO.getToken(), "JWT token不能为空");
        ResponseCookie responseCookie = ResponseCookie
                .from(Objects.requireNonNull(jwtCookieName, "JWT Cookie 名称不能为空"), token)
                .httpOnly(true)
                .secure(jwtCookieSecure)
                .path(jwtCookiePath)
                .sameSite(jwtCookieSameSite)
                .maxAge(loginResponseDTO.getExpiresIn())
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }

    /**
     * 清除 JWT Cookie。
     *
     * @param response 当前响应
     */
    private void clearTokenCookie(HttpServletResponse response) {
        ResponseCookie responseCookie = ResponseCookie
                .from(Objects.requireNonNull(jwtCookieName, "JWT Cookie 名称不能为空"), "")
                .httpOnly(true)
                .secure(jwtCookieSecure)
                .path(jwtCookiePath)
                .sameSite(jwtCookieSameSite)
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }
}
