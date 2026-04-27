package com.graProject.graBackend.service;

import com.graProject.graBackend.dto.LoginRequestDTO;
import com.graProject.graBackend.dto.LoginResponseDTO;
import com.graProject.graBackend.dto.RegisterRequestDto;

/**
 * 用户服务。
 */
public interface UserService {

    /**
     * 用户登录。
     *
     * @param loginRequestDTO 登录请求
     * @return 登录成功后的令牌及用户信息
     */
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    /**
     * 用户注册。
     * 对注册信息进行业务处理，成功时返回注册成功提示。
     *
     * @param registerRequestDto 注册请求
     * @return 注册结果
     */
    String register(RegisterRequestDto registerRequestDto);
}
