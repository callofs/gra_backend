package com.graProject.graBackend.common.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.graProject.graBackend.config.OssProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 阿里云 OSS 工具类。
 */
@Component
public class AliyunOssUtil {

    private final OSS ossClient;
    private final OssProperties ossProperties;

    public AliyunOssUtil(OSS ossClient, OssProperties ossProperties) {
        this.ossClient = ossClient;
        this.ossProperties = ossProperties;
    }

    /**
     * 上传对象。
     *
     * @param objectKey OSS objectKey
     * @param bytes     对象内容
     */
    public void putObject(String objectKey, byte[] bytes) {
        ossClient.putObject(ossProperties.getBucket(), objectKey, new java.io.ByteArrayInputStream(bytes));
    }

    /**
     * 下载对象为字节数组。
     *
     * @param objectKey OSS objectKey
     * @return 对象字节数组
     */
    public byte[] getObjectBytes(String objectKey) {
        OSSObject ossObject = ossClient.getObject(ossProperties.getBucket(), objectKey);
        try (var in = ossObject.getObjectContent()) {
            return in.readAllBytes();
        } catch (IOException e) {
            throw new IllegalStateException("OSS 文件读取失败", e);
        }
    }
}
