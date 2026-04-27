package com.graProject.graBackend.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一返回对象。
 *
 * @param <T> 返回数据类型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpResult<T> {

  /**
   * 状态码。
   */
  private int code; // 状态码

  /**
   * 提示信息。
   */
  private String message; // 提示信息

  /**
   * 返回数据。
   */
  private T data; // 返回数据

  /**
   * 成功（无返回数据）。
   *
   * @param <T> 返回数据类型
   * @return 统一返回对象
   */
  public static <T> HttpResult<T> success() {
    return new HttpResult<>(HttpCode.SUCCESS.getCode(), HttpCode.SUCCESS.getMessage(), null);
  }

  /**
   * 成功（返回数据）。
   *
   * @param data 返回数据
   * @param <T>  返回数据类型
   * @return 统一返回对象
   */
  public static <T> HttpResult<T> success(T data) {
    return new HttpResult<>(HttpCode.SUCCESS.getCode(), HttpCode.SUCCESS.getMessage(), data);
  }

  /**
   * 成功（自定义提示信息 + 返回数据）。
   *
   * @param message 提示信息
   * @param data    返回数据
   * @param <T>     返回数据类型
   * @return 统一返回对象
   */
  public static <T> HttpResult<T> success(String message, T data) {
    return new HttpResult<>(HttpCode.SUCCESS.getCode(), message, data);
  }

  /**
   * 失败（使用默认失败提示）。
   *
   * @param <T> 返回数据类型
   * @return 统一返回对象
   */
  public static <T> HttpResult<T> fail() {
    return new HttpResult<>(HttpCode.FAILED.getCode(), HttpCode.FAILED.getMessage(), null);
  }

  /**
   * 失败（自定义提示信息）。
   *
   * @param message 提示信息
   * @param <T>     返回数据类型
   * @return 统一返回对象
   */
  public static <T> HttpResult<T> fail(String message) {
    return new HttpResult<>(HttpCode.FAILED.getCode(), message, null);
  }

  /**
   * 自定义状态码（无返回数据）。
   *
   * @param httpCode 状态码枚举
   * @param <T>      返回数据类型
   * @return 统一返回对象
   */
  public static <T> HttpResult<T> of(HttpCode httpCode) {
    return new HttpResult<>(httpCode.getCode(), httpCode.getMessage(), null);
  }

  /**
   * 自定义状态码（自定义提示信息，无返回数据）。
   *
   * @param httpCode 状态码枚举
   * @param message  提示信息
   * @param <T>      返回数据类型
   * @return 统一返回对象
   */
  public static <T> HttpResult<T> of(HttpCode httpCode, String message) {
    return new HttpResult<>(httpCode.getCode(), message, null);
  }

  /**
   * 自定义状态码（带返回数据）。
   *
   * @param httpCode 状态码枚举
   * @param data     返回数据
   * @param <T>      返回数据类型
   * @return 统一返回对象
   */
  public static <T> HttpResult<T> of(HttpCode httpCode, T data) {
    return new HttpResult<>(httpCode.getCode(), httpCode.getMessage(), data);
  }

  /**
   * 自定义状态码（自定义提示信息 + 返回数据）。
   *
   * @param httpCode 状态码枚举
   * @param message  提示信息
   * @param data     返回数据
   * @param <T>      返回数据类型
   * @return 统一返回对象
   */
  public static <T> HttpResult<T> of(HttpCode httpCode, String message, T data) {
    return new HttpResult<>(httpCode.getCode(), message, data);
  }
}
