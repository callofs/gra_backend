package com.graProject.graBackend.service;

import com.graProject.graBackend.dto.LoginRequestDTO;
import com.graProject.graBackend.dto.LoginResponseDTO;
import com.graProject.graBackend.dto.RegisterRequestDto;
import com.graProject.graBackend.dto.UserDTO;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 修改当前用户头像。
     *
     * 接收图片文件并以二进制形式写入用户表的 avatar 字段。
     *
     * @param loginUser 当前登录用户
     * @param file      头像图片文件
     * @return 修改结果提示
     */
    String updateAvatar(UserDTO loginUser, MultipartFile file);

    /**
     * 获取当前登录用户的完整资料。
     *
     * 根据当前登录用户 ID 从数据库读取最新用户信息并返回。
     *
     * @param loginUser 当前登录用户
     * @return 当前登录用户完整资料
     */
    UserDTO getCurrentUserProfile(UserDTO loginUser);

    /**
     * 获取当前登录用户头像二进制数据。
     *
     * @param loginUser 当前登录用户
     * @return 头像二进制数据
     */
    byte[] getCurrentUserAvatar(UserDTO loginUser);
}
