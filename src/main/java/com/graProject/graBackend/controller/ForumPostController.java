package com.graProject.graBackend.controller;

import com.graProject.graBackend.common.annotation.HasPermission;
import com.graProject.graBackend.common.utils.AliyunOssUtil;
import com.graProject.graBackend.common.utils.UploadFileUtil;
import com.graProject.graBackend.common.result.HttpCode;
import com.graProject.graBackend.common.result.HttpResult;
import com.graProject.graBackend.dto.ForumPostAuditRequestDTO;
import com.graProject.graBackend.dto.ForumPostCreateRequestDTO;
import com.graProject.graBackend.dto.ForumPostDTO;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.entity.ForumPostDO;
import com.graProject.graBackend.service.ForumPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 主题论坛贴文接口。
 */
@RestController
@RequestMapping("/api/forumPost")
@Tag(name = "论坛贴文接口")
public class ForumPostController {

    /**
     * 论坛贴文服务。
     */
    private final ForumPostService forumPostService;

    /**
     * 阿里云 OSS 工具类。
     */
    private final AliyunOssUtil aliyunOssUtil;

    /**
     * 构造方法。
     *
     * @param forumPostService 论坛贴文服务
     * @param aliyunOssUtil    阿里云 OSS 工具类
     */
    public ForumPostController(ForumPostService forumPostService, AliyunOssUtil aliyunOssUtil) {
        this.forumPostService = forumPostService;
        this.aliyunOssUtil = aliyunOssUtil;
    }

    /**
     * 发布贴文。
     *
     * <p>
     * 贴文正文为 HTML 富文本（WangEditor 产出），其中图片应通过 {@code /api/forumPost/images}
     * 上传后插入编辑器，后端仅保存正文字符串。
     * </p>
     *
     * @param createRequestDTO 发布贴文请求
     * @param request          当前请求（用于读取登录用户信息）
     * @return 发布结果
     */
    @Operation(summary = "发布贴文")
    @PostMapping("/uploadForum")
    public HttpResult<String> createForumPost(@RequestBody ForumPostCreateRequestDTO createRequestDTO,
            HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO) || userDTO.getId() == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        ForumPostDO forumPostDO = new ForumPostDO();
        forumPostDO.setUserId(userDTO.getId());
        if (createRequestDTO != null) {
            forumPostDO.setSectionCode(createRequestDTO.getSectionCode());
            forumPostDO.setTitle(createRequestDTO.getTitle());
            forumPostDO.setContent(createRequestDTO.getContent());
            forumPostDO.setCoverImages(createRequestDTO.getCoverImages());
            forumPostDO.setIsAnonymous(createRequestDTO.getIsAnonymous());
        }
        return HttpResult.success(forumPostService.uploadForumPost(forumPostDO));
    }

    /**
     * 获取贴文详情。
     *
     * <p>
     * 该接口用于贴文详情页展示，必须登录后访问，否则返回 401。
     * </p>
     *
     * @param id      贴文 ID
     * @param request 当前请求（用于读取登录用户信息）
     * @return 贴文详情
     */
    @Operation(summary = "获取贴文详情")
    @GetMapping("/getForum/{id}")
    public HttpResult<ForumPostDTO> getForumPostDetail(@PathVariable("id") Long id, HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO) || userDTO.getId() == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        ForumPostDTO dto = forumPostService.getForumPostDetail(id);
        if (dto == null) {
            return HttpResult.of(HttpCode.NOT_FOUND, "贴文不存在", null);
        }
        return HttpResult.success(dto);
    }

    /**
     * 分页获取贴文摘要列表（公开）。
     *
     * <p>
     * 该接口用于贴文列表页展示，未登录可访问。返回内容中的 {@code content} 为摘要文本（非完整 HTML）。
     * </p>
     *
     * @param page        页码（从 1 开始）
     * @param size        每页条数（服务端会做上限保护）
     * @param sectionCode 板块编码（可选）
     * @param keyword     标题关键字（可选）
     * @return 分页摘要列表
     */
    @Operation(summary = "分页获取贴文摘要列表（公开）")
    @GetMapping("/summary")
    public HttpResult<IPage<ForumPostDTO>> listForumPostSummaries(
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "10") long size,
            @RequestParam(value = "sectionCode", required = false) String sectionCode,
            @RequestParam(value = "keyword", required = false) String keyword) {
        return HttpResult.success(forumPostService.listForumPostSummaries(page, size, sectionCode, keyword));
    }

    /**
     * 管理员分页查询贴文审核列表。
     *
     * @param page    页码（从 1 开始）
     * @param size    每页条数
     * @param status  贴文状态（可选）
     * @param keyword 标题关键字（可选）
     * @return 贴文审核分页列表
     */
    @HasPermission(roles = { 3 })
    @Operation(summary = "管理员分页查询贴文审核列表")
    @GetMapping("/admin/audit/list")
    public HttpResult<IPage<ForumPostDTO>> listForumPostsForAudit(
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "10") long size,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "keyword", required = false) String keyword) {
        return HttpResult.success(forumPostService.listForumPostsForAudit(page, size, status, keyword));
    }

    /**
     * 管理员审核贴文。
     *
     * @param requestDTO 审核请求参数
     * @return 审核结果
     */
    @HasPermission(roles = { 3 })
    @Operation(summary = "管理员审核贴文")
    @PostMapping("/admin/audit")
    public HttpResult<String> auditForumPost(@RequestBody ForumPostAuditRequestDTO requestDTO) {
        if (requestDTO == null) {
            return HttpResult.of(HttpCode.BAD_REQUEST, "审核参数不能为空", null);
        }
        return HttpResult.success(forumPostService.auditForumPost(requestDTO.getPostId(), requestDTO.getStatus()));
    }

    /**
     * 查询当前登录用户发布的贴文审核状态。
     *
     * @param page    页码（从 1 开始）
     * @param size    每页条数
     * @param request 当前请求
     * @return 当前用户贴文审核状态分页列表
     */
    @HasPermission(roles = { 1, 2, 3 })
    @Operation(summary = "查询当前用户贴文审核状态")
    @GetMapping("/my/audit-status")
    public HttpResult<IPage<ForumPostDTO>> listMyForumPosts(
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "10") long size,
            HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO) || userDTO.getId() == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        return HttpResult.success(forumPostService.listMyForumPosts(userDTO, page, size));
    }

    /**
     * WangEditor 上传贴文图片。
     *
     * <p>
     * 图片上传到 OSS（私有读），返回后端代理访问地址供编辑器插入正文 HTML。
     * </p>
     *
     * @param file    上传的图片文件
     * @param request 当前请求（用于读取登录用户信息）
     * @return WangEditor v5 约定的返回结构
     */
    @Operation(summary = "WangEditor 上传图片")
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> uploadPostImage(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO) || userDTO.getId() == null) {
            return wangEditorError("未登录");
        }

        String ext = UploadFileUtil.getExtensionLower(file.getOriginalFilename());
        if (ext == null || ext.isBlank()) {
            return wangEditorError("文件格式不支持");
        }
        byte[] bytes;
        try {
            bytes = UploadFileUtil.readBytes(file, Set.of("png", "jpg", "jpeg", "gif", "webp"), 5L * 1024 * 1024);
        } catch (Exception e) {
            return wangEditorError(e.getMessage());
        }

        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String objectKey = "forum_post/" + userDTO.getId() + "/" + date + "/" + UUID.randomUUID() + "." + ext;
        aliyunOssUtil.putObject(objectKey, bytes);

        String url = "/api/forumPost/images/view?key=" + URLEncoder.encode(objectKey, StandardCharsets.UTF_8);
        Map<String, Object> data = new HashMap<>();
        data.put("url", url);

        Map<String, Object> result = new HashMap<>();
        result.put("errno", 0);
        result.put("data", data);
        return result;
    }

    /**
     * 论坛贴文图片预览（后端代理 OSS 私有读）。
     *
     * <p>
     * 由于 OSS 为私有读，前端无法直接访问，因此通过后端代理拉取并返回图片二进制流。
     * </p>
     *
     * @param objectKey OSS 对象 Key
     * @return 图片二进制流响应
     */
    @Operation(summary = "论坛贴文图片预览（后端代理 OSS 私有读）")
    @GetMapping(value = "/images/view")
    public ResponseEntity<byte[]> viewPostImage(@RequestParam("key") String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        byte[] bytes;
        try {
            bytes = aliyunOssUtil.getObjectBytes(objectKey);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        MediaType mediaType = guessImageMediaType(objectKey);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .contentLength(bytes.length)
                .body(bytes);
    }

    /**
     * 根据文件后缀猜测图片的 MediaType。
     *
     * @param objectKey OSS 对象 Key
     * @return 图片 MediaType
     */
    private static MediaType guessImageMediaType(String objectKey) {
        String ext = UploadFileUtil.getExtensionLower(objectKey);
        return switch (ext) {
            case "png" -> MediaType.IMAGE_PNG;
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "gif" -> MediaType.IMAGE_GIF;
            case "webp" -> MediaType.parseMediaType("image/webp");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    /**
     * WangEditor v5 上传失败响应体。
     *
     * @param message 错误信息
     * @return WangEditor 约定的失败返回结构
     */
    private static Map<String, Object> wangEditorError(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("errno", 1);
        result.put("message", message == null ? "上传失败" : message);
        return result;
    }
}
