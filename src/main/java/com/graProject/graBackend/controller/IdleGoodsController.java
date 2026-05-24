package com.graProject.graBackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graProject.graBackend.common.utils.AliyunOssUtil;
import com.graProject.graBackend.common.utils.UploadFileUtil;
import com.graProject.graBackend.common.result.HttpCode;
import com.graProject.graBackend.common.result.HttpResult;
import com.graProject.graBackend.dto.IdleGoodsCreateRequestDTO;
import com.graProject.graBackend.dto.IdleGoodsDTO;
import com.graProject.graBackend.dto.IdleGoodsUpdateRequestDTO;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.entity.IdleGoodsDO;
import com.graProject.graBackend.service.IdleGoodsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 闲置物品接口。
 */
@RestController
@RequestMapping("/api/idleGoods")
@Tag(name = "闲置物品接口")
public class IdleGoodsController {

    private final IdleGoodsService idleGoodsService;
    private final AliyunOssUtil aliyunOssUtil;

    public IdleGoodsController(IdleGoodsService idleGoodsService, AliyunOssUtil aliyunOssUtil) {
        this.idleGoodsService = idleGoodsService;
        this.aliyunOssUtil = aliyunOssUtil;
    }

    /**
     * 发布闲置物品。
     *
     * @param createRequestDTO 发布请求
     * @param request          当前请求
     * @return 发布结果
     */
    @Operation(summary = "发布闲置物品")
    @PostMapping("/publish")
    public HttpResult<String> createIdleGoods(@RequestBody IdleGoodsCreateRequestDTO createRequestDTO,
            HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO) || userDTO.getId() == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        IdleGoodsDO idleGoodsDO = new IdleGoodsDO();
        idleGoodsDO.setUserId(userDTO.getId());
        if (createRequestDTO != null) {
            idleGoodsDO.setGoodsTypeCode(createRequestDTO.getGoodsTypeCode());
            idleGoodsDO.setTitle(createRequestDTO.getTitle());
            idleGoodsDO.setName(createRequestDTO.getName());
            idleGoodsDO.setCoverImages(createRequestDTO.getCoverImages());
            idleGoodsDO.setDescription(createRequestDTO.getDescription());
            idleGoodsDO.setFitAge(createRequestDTO.getFitAge());
            idleGoodsDO.setSize(createRequestDTO.getSize());
            idleGoodsDO.setSeason(createRequestDTO.getSeason());
            idleGoodsDO.setMaterial(createRequestDTO.getMaterial());
            idleGoodsDO.setOldDegree(createRequestDTO.getOldDegree());
            idleGoodsDO.setPickUpType(createRequestDTO.getPickUpType());
            idleGoodsDO.setAddress(createRequestDTO.getAddress());
        }
        return HttpResult.success(idleGoodsService.createIdleGoods(idleGoodsDO));
    }

    /**
     * 分页获取闲置物品列表。
     *
     * @param page          页码
     * @param size          每页条数
     * @param goodsTypeCode 物品类型编码
     * @param keyword       标题或名称关键字
     * @return 分页结果
     */
    @Operation(summary = "分页获取闲置物品列表")
    @GetMapping("/list")
    public HttpResult<IPage<IdleGoodsDTO>> listIdleGoods(
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "10") long size,
            @RequestParam(value = "goodsTypeCode", required = false) String goodsTypeCode,
            @RequestParam(value = "keyword", required = false) String keyword) {
        return HttpResult.success(idleGoodsService.listIdleGoods(page, size, goodsTypeCode, keyword));
    }

    /**
     * 获取闲置物品详情。
     *
     * @param id 闲置物品 ID
     * @return 详情
     */
    @Operation(summary = "获取闲置物品详情")
    @GetMapping("/detail/{id}")
    public HttpResult<IdleGoodsDTO> getIdleGoodsDetail(@PathVariable("id") Long id) {
        IdleGoodsDTO dto = idleGoodsService.getIdleGoodsDetail(id);
        if (dto == null) {
            return HttpResult.of(HttpCode.NOT_FOUND, "闲置物品不存在", null);
        }
        return HttpResult.success(dto);
    }

    /**
     * 修改闲置物品。
     *
     * @param id               闲置物品 ID
     * @param updateRequestDTO 修改请求
     * @param request          当前请求
     * @return 修改结果
     */
    @Operation(summary = "修改闲置物品")
    @PostMapping("/update/{id}")
    public HttpResult<String> updateIdleGoods(@PathVariable("id") Long id,
            @RequestBody IdleGoodsUpdateRequestDTO updateRequestDTO,
            HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO) || userDTO.getId() == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        IdleGoodsDO idleGoodsDO = new IdleGoodsDO();
        idleGoodsDO.setId(id);
        if (updateRequestDTO != null) {
            idleGoodsDO.setGoodsTypeCode(updateRequestDTO.getGoodsTypeCode());
            idleGoodsDO.setTitle(updateRequestDTO.getTitle());
            idleGoodsDO.setName(updateRequestDTO.getName());
            idleGoodsDO.setCoverImages(updateRequestDTO.getCoverImages());
            idleGoodsDO.setDescription(updateRequestDTO.getDescription());
            idleGoodsDO.setFitAge(updateRequestDTO.getFitAge());
            idleGoodsDO.setSize(updateRequestDTO.getSize());
            idleGoodsDO.setSeason(updateRequestDTO.getSeason());
            idleGoodsDO.setMaterial(updateRequestDTO.getMaterial());
            idleGoodsDO.setOldDegree(updateRequestDTO.getOldDegree());
            idleGoodsDO.setPickUpType(updateRequestDTO.getPickUpType());
            idleGoodsDO.setAddress(updateRequestDTO.getAddress());
            idleGoodsDO.setStatus(updateRequestDTO.getStatus());
        }
        return HttpResult.success(idleGoodsService.updateIdleGoods(userDTO, idleGoodsDO));
    }

    /**
     * 获取我的闲置物品列表。
     *
     * @param page    页码
     * @param size    每页条数
     * @param request 当前请求
     * @return 分页结果
     */
    @Operation(summary = "获取我的闲置物品列表")
    @GetMapping("/my/list")
    public HttpResult<IPage<IdleGoodsDTO>> listMyIdleGoods(
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "10") long size,
            HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO) || userDTO.getId() == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        return HttpResult.success(idleGoodsService.listMyIdleGoods(userDTO, page, size));
    }

    /**
     * 上传闲置物品图片。
     *
     * @param file    上传文件
     * @param request 当前请求
     * @return WangEditor 约定返回结构
     */
    @Operation(summary = "上传闲置物品图片")
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> uploadIdleGoodsImage(@RequestPart("file") MultipartFile file,
            HttpServletRequest request) {
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
        String objectKey = "idle_goods/" + userDTO.getId() + "/" + date + "/" + UUID.randomUUID() + "." + ext;
        aliyunOssUtil.putObject(objectKey, bytes);

        String url = "/api/idleGoods/images/view?key=" + URLEncoder.encode(objectKey, StandardCharsets.UTF_8);
        Map<String, Object> data = new HashMap<>();
        data.put("url", url);

        Map<String, Object> result = new HashMap<>();
        result.put("errno", 0);
        result.put("data", data);
        return result;
    }

    /**
     * 预览闲置物品图片。
     *
     * @param objectKey OSS 对象 Key
     * @return 图片响应
     */
    @Operation(summary = "预览闲置物品图片")
    @GetMapping(value = "/images/view")
    public ResponseEntity<byte[]> viewIdleGoodsImage(@RequestParam("key") String objectKey) {
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
