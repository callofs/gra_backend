package com.graProject.graBackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OSS 配置。
 */
@ConfigurationProperties(prefix = "oss")
public class OssProperties {

    /**
     * OSS Endpoint。
     */
    private String endpoint;

    /**
     * OSS Bucket。
     */
    private String bucket;

    /**
     * OSS AccessKeyId。
     */
    private String accessKeyId;

    /**
     * OSS AccessKeySecret。
     */
    private String accessKeySecret;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }
}
