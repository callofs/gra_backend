package com.graProject.graBackend.common.result;

/**
 * 统一返回状态码枚举。
 */
public enum HttpCode {
    /**
     * 成功。
     */
    SUCCESS(200, "成功"),
    /**
     * 请求参数错误。
     */
    BAD_REQUEST(400, "请求参数错误"),
    /**
     * 未登录或登录已过期。
     */
    UNAUTHORIZED(401, "未登录或登录已过期"),
    /**
     * 无权限访问。
     */
    FORBIDDEN(403, "无权限访问"),
    /**
     * 资源不存在。
     */
    NOT_FOUND(404, "资源不存在"),
    /**
     * 服务器内部错误。
     */
    FAILED(500, "服务器内部错误");

    /**
     * 状态码。
     */
    private final int code;

    /**
     * 默认提示信息。
     */
    private final String message;

    /**
     * 构造函数。
     *
     * @param code    状态码
     * @param message 默认提示信息
     */
    HttpCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取状态码。
     *
     * @return 状态码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取默认提示信息。
     *
     * @return 默认提示信息
     */
    public String getMessage() {
        return message;
    }
}
