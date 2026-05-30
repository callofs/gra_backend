package com.graProject.graBackend.controller;

import com.graProject.graBackend.common.exception.User.UserLoginException;
import com.graProject.graBackend.common.result.HttpCode;
import com.graProject.graBackend.common.result.HttpResult;
import com.graProject.graBackend.dto.LoginRequestDTO;
import com.graProject.graBackend.dto.LoginResponseDTO;
import com.graProject.graBackend.dto.RegisterRequestDto;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.dto.FileDownloadDTO;
import com.graProject.graBackend.dto.ExpertCertificationMaterialDTO;
import com.graProject.graBackend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
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
     * 根据用户 ID 获取用户信息。
     *
     * @param userId 用户 ID
     * @return 用户信息
     */
    @Operation(summary = "根据用户ID获取用户信息")
    @GetMapping("/getUserById/{userId}")
    public HttpResult<UserDTO> getUserProfileById(@PathVariable("userId") Long userId, HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        UserDTO currentUser = loginUser instanceof UserDTO userDTO ? userDTO : null;
        UserDTO userDTO = userService.getUserProfileById(currentUser, userId);
        if (userDTO == null) {
            return HttpResult.of(HttpCode.NOT_FOUND, "用户不存在", null);
        }
        return HttpResult.success(userDTO);
    }

    /**
     * 获取专家用户列表。
     *
     * @param request 当前请求
     * @return 专家用户列表
     */
    @Operation(summary = "获取专家用户列表")
    @GetMapping("/get/experts")
    public HttpResult<List<UserDTO>> listExpertUsers(
            HttpServletRequest request,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        Object loginUser = request.getAttribute("loginUser");
        UserDTO currentUser = loginUser instanceof UserDTO userDTO ? userDTO : null;
        return HttpResult.success(userService.listExpertUsers(currentUser, limit));
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
        FileDownloadDTO download;
        try {
            download = userService.getCurrentUserCertificationMaterial(userDTO);
        } catch (UserLoginException e) {
            HttpStatus status;
            try {
                status = HttpStatus.valueOf(e.getHttpCode().getCode());
            } catch (Exception ignore) {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
            return ResponseEntity.status(status)
                    .header("X-Message", e.getMessage())
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Message", "服务器内部错误")
                    .build();
        }
        if (download == null || download.getBytes() == null || download.getBytes().length == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .header("X-Message", "用户未上传认证材料")
                    .build();
        }
        String filename = (download.getFilename() == null || download.getFilename().isBlank())
                ? "certification"
                : download.getFilename();
        String contentType = (download.getContentType() == null || download.getContentType().isBlank())
                ? MediaType.APPLICATION_OCTET_STREAM_VALUE
                : download.getContentType();
        byte[] bytes = download.getBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build().toString())
                .contentLength(bytes.length)
                .body(bytes);
    }

    /**
     * 管理员获取指定用户的专家认证材料。
     *
     * @param userId  被查看的用户 ID
     * @param request 当前请求
     * @return 认证材料二进制流响应
     */
    @Operation(summary = "管理员获取指定用户认证材料")
    @GetMapping(value = "/admin/certificationMaterial/{userId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getUserCertificationMaterialByAdmin(@PathVariable("userId") Long userId,
            HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        FileDownloadDTO download;
        try {
            download = userService.getUserCertificationMaterialByAdmin(userDTO, userId);
        } catch (UserLoginException e) {
            HttpStatus status;
            try {
                status = HttpStatus.valueOf(e.getHttpCode().getCode());
            } catch (Exception ignore) {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
            return ResponseEntity.status(status)
                    .header("X-Message", e.getMessage())
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Message", "服务器内部错误")
                    .build();
        }
        if (download == null || download.getBytes() == null || download.getBytes().length == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .header("X-Message", "用户未上传认证材料")
                    .build();
        }
        String filename = (download.getFilename() == null || download.getFilename().isBlank())
                ? "certification"
                : download.getFilename();
        String contentType = (download.getContentType() == null || download.getContentType().isBlank())
                ? MediaType.APPLICATION_OCTET_STREAM_VALUE
                : download.getContentType();
        byte[] bytes = download.getBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build().toString())
                .contentLength(bytes.length)
                .body(bytes);
    }

    /**
     * 关注指定用户。
     *
     * @param followedId 被关注用户 ID
     * @param request    当前请求
     * @return 操作结果
     */
    @Operation(summary = "关注用户")
    @PostMapping("/follow")
    public HttpResult<String> followUser(@RequestParam("followedId") Long followedId, HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO) || userDTO.getId() == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        userService.followUser(userDTO, followedId);
        return HttpResult.success("关注成功");
    }

    /**
     * 取消关注指定用户。
     *
     * @param followedId 被取消关注用户 ID
     * @param request    当前请求
     * @return 操作结果
     */
    @Operation(summary = "取消关注用户")
    @PostMapping("/unfollow")
    public HttpResult<String> unfollowUser(@RequestParam("followedId") Long followedId, HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO) || userDTO.getId() == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        userService.unfollowUser(userDTO, followedId);
        return HttpResult.success("取消关注成功");
    }

    /**
     * 获取当前登录用户关注的用户列表。
     *
     * @param request 当前请求
     * @return 关注用户列表
     */
    @Operation(summary = "获取我的关注用户列表")
    @GetMapping("/my/follows")
    public HttpResult<java.util.List<UserDTO>> listMyFollowedUsers(HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO) || userDTO.getId() == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        return HttpResult.success(userService.listMyFollowedUsers(userDTO));
    }

    /**
     * 获取当前登录用户的粉丝列表。
     *
     * @param request 当前请求
     * @return 粉丝用户列表
     */
    @Operation(summary = "获取我的粉丝列表")
    @GetMapping("/my/followers")
    public HttpResult<java.util.List<UserDTO>> listMyFollowers(HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO) || userDTO.getId() == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        return HttpResult.success(userService.listMyFollowers(userDTO));
    }

    /**
     * 管理员修改用户角色。
     *
     * @param userId  被修改的用户 ID
     * @param role    新角色：1=普通家长 2=认证专家 3=平台管理员
     * @param request 当前请求
     * @return 操作结果
     */
    @Operation(summary = "管理员修改用户角色")
    @PostMapping("/admin/role/{userId}")
    public HttpResult<String> updateUserRoleByAdmin(@PathVariable("userId") Long userId,
            @RequestParam("role") Integer role, HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO)) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        try {
            userService.updateUserRoleByAdmin(userDTO, userId, role);
            return HttpResult.success("角色修改成功");
        } catch (UserLoginException e) {
            return HttpResult.of(e.getHttpCode(), e.getMessage(), null);
        } catch (Exception e) {
            return HttpResult.fail("角色修改失败");
        }
    }

    /**
     * 管理员获取专家认证材料列表。
     *
     * @param request 当前请求
     * @return 专家认证材料列表
     */
    @Operation(summary = "管理员获取专家认证材料列表")
    @GetMapping("/admin/certificationMaterials")
    public HttpResult<java.util.List<ExpertCertificationMaterialDTO>> listExpertCertificationMaterials(
            HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO)) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        try {
            return HttpResult.success(userService.listExpertCertificationMaterials(userDTO));
        } catch (UserLoginException e) {
            return HttpResult.of(e.getHttpCode(), e.getMessage(), null);
        } catch (Exception e) {
            return HttpResult.fail("获取列表失败");
        }
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
