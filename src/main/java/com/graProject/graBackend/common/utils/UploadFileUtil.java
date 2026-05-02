package com.graProject.graBackend.common.utils;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

/**
 * 文件上传工具类。
 *
 * 用于对上传文件进行基本校验（是否为空、扩展名是否允许、大小限制）并读取为字节数组，
 * 便于在业务层统一复用。
 */
public class UploadFileUtil {

    /**
     * 读取上传文件为字节数组，并校验扩展名和大小。
     *
     * @param file           上传文件
     * @param allowExtLower  允许的扩展名集合（小写，不带点），例如：pdf/doc/docx
     * @param maxSizeBytes   最大允许大小（字节），小于等于 0 表示不限制
     * @return 文件字节数组
     */
    public static byte[] readBytes(MultipartFile file, Set<String> allowExtLower, long maxSizeBytes) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String ext = getExtensionLower(originalFilename);
        if (allowExtLower != null && !allowExtLower.isEmpty()) {
            if (!StringUtils.hasText(ext) || !allowExtLower.contains(ext)) {
                throw new IllegalArgumentException("文件格式不支持");
            }
        }

        if (maxSizeBytes > 0 && file.getSize() > maxSizeBytes) {
            throw new IllegalArgumentException("文件大小超出限制");
        }

        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new IllegalStateException("文件读取失败", e);
        }
    }

    /**
     * 获取文件扩展名（小写、不带点）。
     *
     * @param filename 文件名
     * @return 扩展名（小写、不带点），无法解析时返回空字符串
     */
    public static String getExtensionLower(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }

    private UploadFileUtil() {
    }
}
