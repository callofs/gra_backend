package com.graProject.graBackend.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * OSS 客户端配置。
 */
@Configuration
@EnableConfigurationProperties(OssProperties.class)
public class OssConfig {

    /**
     * 构建阿里云 OSS 客户端。
     *
     * @param ossProperties OSS 配置
     * @return OSS 客户端
     */
    @Bean(destroyMethod = "shutdown")
    public OSS ossClient(OssProperties ossProperties) {
        if (!StringUtils.hasText(ossProperties.getEndpoint())
                || !StringUtils.hasText(ossProperties.getAccessKeyId())
                || !StringUtils.hasText(ossProperties.getAccessKeySecret())) {
            throw new IllegalStateException("OSS 配置不完整，请检查 oss.endpoint/oss.access-key-id/oss.access-key-secret");
        }
        return new OSSClientBuilder().build(
                ossProperties.getEndpoint(),
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret()
        );
    }
}
