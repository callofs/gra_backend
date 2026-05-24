package com.graProject.graBackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graProject.graBackend.common.utils.AliyunOssUtil;
import com.graProject.graBackend.common.utils.UploadFileUtil;
import com.graProject.graBackend.common.result.HttpCode;
import com.graProject.graBackend.common.result.HttpResult;
import com.graProject.graBackend.dto.ExpertLectureDTO;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.service.ExpertLectureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@Tag(name = "专家讲座接口")
@RequestMapping("/api/lecture")
public class ExpertLectureController {

    private final ExpertLectureService expertLectureService;
    private final AliyunOssUtil aliyunOssUtil;

    public ExpertLectureController(ExpertLectureService expertLectureService, AliyunOssUtil aliyunOssUtil) {
        this.expertLectureService = expertLectureService;
        this.aliyunOssUtil = aliyunOssUtil;
    }

    @Operation(summary = "创建讲座")
    @PostMapping("/create")
    public HttpResult<ExpertLectureDTO> createLecture(
            HttpServletRequest request,
            @RequestBody ExpertLectureDTO lectureDTO) {
        Object loginUser = request.getAttribute("loginUser");
        UserDTO currentUser = loginUser instanceof UserDTO userDTO ? userDTO : null;
        if (currentUser == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        try {
            ExpertLectureDTO result = expertLectureService.createLecture(currentUser, lectureDTO);
            return HttpResult.success(result);
        } catch (RuntimeException e) {
            return HttpResult.fail(e.getMessage());
        }
    }

    @Operation(summary = "更新讲座")
    @PutMapping("/update")
    public HttpResult<String> updateLecture(
            HttpServletRequest request,
            @RequestBody ExpertLectureDTO lectureDTO) {
        Object loginUser = request.getAttribute("loginUser");
        UserDTO currentUser = loginUser instanceof UserDTO userDTO ? userDTO : null;
        if (currentUser == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        try {
            boolean success = expertLectureService.updateLecture(currentUser, lectureDTO);
            return success ? HttpResult.success("更新成功") : HttpResult.fail("更新失败");
        } catch (RuntimeException e) {
            return HttpResult.fail(e.getMessage());
        }
    }

    @Operation(summary = "删除讲座")
    @PostMapping("/delete/{lectureId}")
    public HttpResult<String> deleteLecture(
            HttpServletRequest request,
            @PathVariable Long lectureId) {
        Object loginUser = request.getAttribute("loginUser");
        UserDTO currentUser = loginUser instanceof UserDTO userDTO ? userDTO : null;
        if (currentUser == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        try {
            boolean success = expertLectureService.deleteLecture(currentUser, lectureId);
            return success ? HttpResult.success("删除成功") : HttpResult.fail("删除失败");
        } catch (RuntimeException e) {
            return HttpResult.fail(e.getMessage());
        }
    }

    @Operation(summary = "获取讲座详情")
    @GetMapping("/detail/{lectureId}")
    public HttpResult<ExpertLectureDTO> getLectureById(@PathVariable Long lectureId) {
        ExpertLectureDTO result = expertLectureService.getLectureById(lectureId);
        if (result == null) {
            return HttpResult.fail("讲座不存在");
        }
        return HttpResult.success(result);
    }

    @Operation(summary = "获取讲座列表")
    @GetMapping("/list")
    public HttpResult<IPage<ExpertLectureDTO>> listLectures(
            @RequestParam("page") long page,
            @RequestParam("size") long size) {
        IPage<ExpertLectureDTO> result = expertLectureService.listLectures(page, size);
        return HttpResult.success(result);
    }

    @Operation(summary = "获取专家的讲座列表")
    @GetMapping("/expert/{expertId}")
    public HttpResult<IPage<ExpertLectureDTO>> listLecturesByExpert(
            @PathVariable Long expertId,
            @RequestParam long page,
            @RequestParam long size) {
        IPage<ExpertLectureDTO> result = expertLectureService.listLecturesByExpert(expertId, page, size);
        return HttpResult.success(result);
    }

    @Operation(summary = "上传讲座图片")
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> uploadLectureImage(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
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
        String objectKey = "expert_lecture/" + userDTO.getId() + "/" + date + "/" + UUID.randomUUID() + "." + ext;
        aliyunOssUtil.putObject(objectKey, bytes);

        String url = "/api/lecture/images/view?key=" + URLEncoder.encode(objectKey, StandardCharsets.UTF_8);
        Map<String, Object> data = new HashMap<>();
        data.put("url", url);

        Map<String, Object> result = new HashMap<>();
        result.put("errno", 0);
        result.put("data", data);
        return result;
    }

    @Operation(summary = "讲座图片预览")
    @GetMapping(value = "/images/view")
    public ResponseEntity<byte[]> viewLectureImage(@RequestParam("key") String objectKey) {
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

    @Operation(summary = "更新讲座状态")
    @PutMapping("/status/{lectureId}")
    public HttpResult<String> updateLectureStatus(
            HttpServletRequest request,
            @PathVariable Long lectureId,
            @RequestParam Integer status) {
        Object loginUser = request.getAttribute("loginUser");
        UserDTO currentUser = loginUser instanceof UserDTO userDTO ? userDTO : null;
        if (currentUser == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        try {
            boolean success = expertLectureService.updateLectureStatus(currentUser, lectureId, status);
            return success ? HttpResult.success("状态更新成功") : HttpResult.fail("状态更新失败");
        } catch (RuntimeException e) {
            return HttpResult.fail(e.getMessage());
        }
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
     * WangEditor 上传失败响应体。
     *
     * @param message 错误信息
     * @return 失败返回结构
     */
    private static Map<String, Object> wangEditorError(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("errno", 1);
        result.put("message", message == null ? "上传失败" : message);
        return result;
    }
}