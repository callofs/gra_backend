package com.graProject.graBackend.common.handler;

import com.graProject.graBackend.common.exception.User.UserLoginException;
import com.graProject.graBackend.common.result.HttpCode;
import com.graProject.graBackend.common.result.HttpResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
