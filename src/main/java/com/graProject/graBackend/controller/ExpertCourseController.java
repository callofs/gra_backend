package com.graProject.graBackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graProject.graBackend.common.annotation.HasPermission;
import com.graProject.graBackend.common.result.HttpCode;
import com.graProject.graBackend.common.result.HttpResult;
import com.graProject.graBackend.common.utils.AliyunOssUtil;
import com.graProject.graBackend.common.utils.UploadFileUtil;
import com.graProject.graBackend.dto.ExpertCourseDTO;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.service.ExpertCourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/expertCourse")
@Tag(name = "专家课程接口")
public class ExpertCourseController {

    private static final Set<String> VIDEO_EXT = Set.of("mp4", "mov", "avi", "mkv", "flv", "wmv");
    private static final Set<String> IMAGE_EXT = Set.of("png", "jpg", "jpeg", "gif", "webp");
    private final ExpertCourseService expertCourseService;
    private final AliyunOssUtil aliyunOssUtil;

    public ExpertCourseController(ExpertCourseService expertCourseService, AliyunOssUtil aliyunOssUtil) {
        this.expertCourseService = expertCourseService;
        this.aliyunOssUtil = aliyunOssUtil;
    }

    @Operation(summary = "上传课程视频文件")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @HasPermission(roles = { 2, 3 })
    public HttpResult<Map<String, String>> uploadCourseFile(@RequestPart("file") MultipartFile file,
            HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO)) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        byte[] bytes;
        try {
            bytes = UploadFileUtil.readBytes(file, VIDEO_EXT, 500L * 1024 * 1024);
        } catch (Exception e) {
            return HttpResult.fail(e.getMessage());
        }
        try {
            String objectKey = expertCourseService.uploadCourseFile(userDTO, file.getOriginalFilename(), bytes);
            return HttpResult.success(Map.of("objectKey", objectKey));
        } catch (Exception e) {
            return HttpResult.fail(e.getMessage());
        }
    }

    @Operation(summary = "上传课程封面图片")
    @PostMapping(value = "/uploadCover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @HasPermission(roles = { 2, 3 })
    public HttpResult<Map<String, String>> uploadCourseCover(@RequestPart("file") MultipartFile file,
            HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO)) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        byte[] bytes;
        try {
            bytes = UploadFileUtil.readBytes(file, IMAGE_EXT, 10L * 1024 * 1024);
        } catch (Exception e) {
            return HttpResult.fail(e.getMessage());
        }
        try {
            String objectKey = expertCourseService.uploadCourseCover(userDTO, file.getOriginalFilename(), bytes);
            return HttpResult.success(Map.of("objectKey", objectKey));
        } catch (Exception e) {
            return HttpResult.fail(e.getMessage());
        }
    }

    @Operation(summary = "专家创建课程")
    @PostMapping("/createCourse")
    @HasPermission(roles = { 2, 3 })
    public HttpResult<ExpertCourseDTO> createCourse(@RequestBody ExpertCourseDTO courseDTO,
            HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO)) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        try {
            return HttpResult.success(expertCourseService.createCourse(userDTO, courseDTO));
        } catch (Exception e) {
            return HttpResult.fail(e.getMessage());
        }
    }

    @Operation(summary = "专家修改课程")
    @PostMapping("/updateCourse")
    public HttpResult<String> updateCourse(@RequestBody ExpertCourseDTO courseDTO, HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO)) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        try {
            boolean success = expertCourseService.updateCourse(userDTO, courseDTO);
            return success ? HttpResult.success("修改成功") : HttpResult.fail("修改失败");
        } catch (Exception e) {
            return HttpResult.fail(e.getMessage());
        }
    }

    @Operation(summary = "专家删除课程")
    @PostMapping("/deleteCourse/{courseId}")
    @HasPermission(roles = { 2, 3 })
    public HttpResult<String> deleteCourse(@PathVariable("courseId") Long courseId, HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO)) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        try {
            boolean success = expertCourseService.deleteCourse(userDTO, courseId);
            return success ? HttpResult.success("删除成功") : HttpResult.fail("删除失败");
        } catch (Exception e) {
            return HttpResult.fail(e.getMessage());
        }
    }

    @Operation(summary = "课程详情")
    @GetMapping("/getCourseDetail/{courseId}")
    public HttpResult<ExpertCourseDTO> getCourseDetail(@PathVariable("courseId") Long courseId,
            HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        UserDTO userDTO = loginUser instanceof UserDTO dto ? dto : null;
        try {
            return HttpResult.success(expertCourseService.getCourseDetail(userDTO, courseId));
        } catch (Exception e) {
            return HttpResult.fail(e.getMessage());
        }
    }

    @Operation(summary = "获取审核通过的课程列表")
    @GetMapping("/approved")
    public HttpResult<IPage<ExpertCourseDTO>> listApprovedCourses(
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "10") long size) {
        return HttpResult.success(expertCourseService.listApprovedCourses(page, size));
    }

    @Operation(summary = "管理员获取所有课程列表")
    @GetMapping("/admin/list")
    @HasPermission(roles = { 3 })
    public HttpResult<IPage<ExpertCourseDTO>> listAllCourses(
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "10") long size,
            @RequestParam(value = "auditStatus", required = false) Integer auditStatus,
            @RequestParam(value = "expertId", required = false) Long expertId,
            @RequestParam(value = "keyword", required = false) String keyword) {
        return HttpResult.success(expertCourseService.listAllCourses(page, size, auditStatus, expertId, keyword));
    }

    @Operation(summary = "获取我创建的课程列表")
    @GetMapping("/my/courseList")
    public HttpResult<IPage<ExpertCourseDTO>> listMyCourses(
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "10") long size,
            HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO)) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        return HttpResult.success(expertCourseService.listMyCourses(userDTO, page, size));
    }

    @Operation(summary = "管理员审核课程")
    @PostMapping("/audit/{courseId}")
    @HasPermission(roles = { 3 })
    public HttpResult<String> auditCourse(@PathVariable("courseId") Long courseId,
            @RequestParam("auditStatus") Integer auditStatus,
            @RequestParam(value = "auditComment", required = false) String auditComment,
            HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO)) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        try {
            boolean success = expertCourseService.auditCourse(userDTO, courseId, auditStatus, auditComment);
            return success ? HttpResult.success("审核完成") : HttpResult.fail("审核失败");
        } catch (Exception e) {
            return HttpResult.fail(e.getMessage());
        }
    }

    @Operation(summary = "课程封面预览")
    @GetMapping("/cover/view")
    public ResponseEntity<byte[]> viewCourseCover(@RequestParam("objectKey") String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        byte[] bytes;
        try {
            bytes = aliyunOssUtil.getObjectBytes(objectKey);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        MediaType mediaType = guessImageMediaType(objectKey);
        MediaType responseType = mediaType != null ? mediaType : MediaType.APPLICATION_OCTET_STREAM;
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .contentType(responseType)
                .contentLength(bytes.length)
                .body(bytes);
    }

    @Operation(summary = "课程视频获取")
    @GetMapping("/video/view")
    public ResponseEntity<byte[]> viewCourseVideo(@RequestParam("objectKey") String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        byte[] bytes;
        try {
            bytes = aliyunOssUtil.getObjectBytes(objectKey);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        MediaType mediaType = guessVideoMediaType(objectKey);
        MediaType responseType = mediaType != null ? mediaType : MediaType.APPLICATION_OCTET_STREAM;
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .contentType(responseType)
                .contentLength(bytes.length)
                .body(bytes);
    }

    private static MediaType guessImageMediaType(String objectKey) {
        String ext = com.graProject.graBackend.common.utils.UploadFileUtil.getExtensionLower(objectKey);
        return switch (ext) {
            case "png" -> MediaType.IMAGE_PNG;
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "gif" -> MediaType.IMAGE_GIF;
            case "webp" -> MediaType.parseMediaType("image/webp");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    private static MediaType guessVideoMediaType(String objectKey) {
        String ext = UploadFileUtil.getExtensionLower(objectKey);
        return switch (ext) {
            case "mp4" -> MediaType.parseMediaType("video/mp4");
            case "mov" -> MediaType.parseMediaType("video/quicktime");
            case "avi" -> MediaType.parseMediaType("video/x-msvideo");
            case "mkv" -> MediaType.parseMediaType("video/x-matroska");
            case "flv" -> MediaType.parseMediaType("video/x-flv");
            case "wmv" -> MediaType.parseMediaType("video/x-ms-wmv");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }
}
