package com.graProject.graBackend.common.exception.User;

import com.graProject.graBackend.common.result.HttpCode;

/**
 * 用户登录相关异常。
 */
public class UserLoginException extends RuntimeException {

    /**
     * 需要返回的状态码。
     */
    private final HttpCode httpCode;

    /**
     * 构造方法。
     *
     * @param httpCode 状态码
     * @param message  异常信息
     */
    public UserLoginException(HttpCode httpCode, String message) {
        super(message);
        this.httpCode = httpCode;
    }

    /**
     * 获取状态码。
     *
     * @return 状态码
     */
    public HttpCode getHttpCode() {
        return httpCode;
    }
}
