package com.graProject.graBackend.common.handler;

import com.graProject.graBackend.common.exception.User.UserLoginException;
import com.graProject.graBackend.common.result.HttpCode;
import com.graProject.graBackend.common.result.HttpResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 用户相关异常统一处理。
 */
@RestControllerAdvice
public class UserExceptionHandler {

  /**
   * 处理用户登录相关异常。
   *
   * @param e   用户登录相关异常
   * @param <T> 返回数据类型
   * @return 统一返回对象
   */
  @ExceptionHandler(UserLoginException.class)
  public <T> HttpResult<T> handleUserLoginException(UserLoginException e) {
    return HttpResult.of(e.getHttpCode(), e.getMessage());
  }

  /**
   * 处理上传文件超过大小限制的异常。
   *
   * @param e   上传大小超限异常
   * @param <T> 返回数据类型
   * @return 统一返回对象
   */
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public <T> HttpResult<T> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
    return HttpResult.of(HttpCode.BAD_REQUEST, "上传文件过大，请更换更小的文件后重试");
  }

  /**
   * 处理未捕获的异常。
   *
   * @param e   异常
   * @param <T> 返回数据类型
   * @return 统一返回对象
   */
  @ExceptionHandler(Exception.class)
  public <T> HttpResult<T> handleException(Exception e) {
    e.printStackTrace();
    return HttpResult.of(HttpCode.FAILED, HttpCode.FAILED.getMessage());
  }

}
