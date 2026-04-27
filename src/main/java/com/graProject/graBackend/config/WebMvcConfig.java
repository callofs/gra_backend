package com.graProject.graBackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Objects;

/**
 * Spring MVC 配置类。
 * 用于注册系统级拦截器。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * JWT 认证拦截器。
     */
    private final HandlerInterceptor jwtAuthInterceptor;

    /**
     * 构造方法。
     *
     * @param jwtAuthInterceptor JWT 认证拦截器
     */
    public WebMvcConfig(HandlerInterceptor jwtAuthInterceptor) {
        this.jwtAuthInterceptor = jwtAuthInterceptor;
    }

    /**
     * 注册 JWT 拦截器，并放行登录、注册及接口文档相关请求。
     *
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(Objects.requireNonNull(jwtAuthInterceptor))
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/user/login",
                        "/api/user/register",
                        "/doc.html",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/error");
    }
}
