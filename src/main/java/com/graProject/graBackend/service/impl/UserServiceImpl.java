package com.graProject.graBackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.hutool.crypto.SmUtil;
import com.graProject.graBackend.common.utils.JwtTokenUtil;
import com.graProject.graBackend.common.utils.UsernameBloomFilterUtil;
import com.graProject.graBackend.common.exception.User.UserLoginException;
import com.graProject.graBackend.common.result.HttpCode;
import com.graProject.graBackend.dto.LoginRequestDTO;
import com.graProject.graBackend.dto.LoginResponseDTO;
import com.graProject.graBackend.dto.RegisterRequestDto;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.entity.UserDO;
import com.graProject.graBackend.mapper.UserMapper;
import com.graProject.graBackend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 用户服务实现。
 */
@Service
public class UserServiceImpl implements UserService {

    /**
     * 用户 Mapper。
     */
    private final UserMapper userMapper;

    /**
     * 用户名布隆过滤器工具。
     */
    private final UsernameBloomFilterUtil usernameBloomFilterUtil;

    /**
     * JWT 工具类。
     */
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * 构造方法。
     *
     * @param userMapper              用户 Mapper
     * @param usernameBloomFilterUtil 用户名布隆过滤器工具
     * @param jwtTokenUtil            JWT 工具类
     */
    public UserServiceImpl(UserMapper userMapper, UsernameBloomFilterUtil usernameBloomFilterUtil,
            JwtTokenUtil jwtTokenUtil) {
        this.userMapper = userMapper;
        this.usernameBloomFilterUtil = usernameBloomFilterUtil;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    /**
     * 用户登录。
     *
     * @param loginRequestDTO 登录请求
     * @return 登录成功后的令牌及用户信息
     */
    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        if (loginRequestDTO == null
                || !StringUtils.hasText(loginRequestDTO.getUsername())
                || !StringUtils.hasText(loginRequestDTO.getPassword())) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "用户名或密码不能为空");
        }

        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<UserDO>()
                .eq(UserDO::getUsername, loginRequestDTO.getUsername())
                .eq(UserDO::getIsDelete, 0)
                .last("limit 1");

        UserDO userDO = userMapper.selectOne(wrapper);
        if (userDO == null) {
            throw new UserLoginException(HttpCode.UNAUTHORIZED, "账号或密码错误");
        }

        if (!SmUtil.sm3(loginRequestDTO.getPassword()).equals(userDO.getPassword())) {
            throw new UserLoginException(HttpCode.UNAUTHORIZED, "账号或密码错误");
        }

        UserDTO userDTO = UserDTO.builder()
                .id(userDO.getId())
                .username(userDO.getUsername())
                .nickname(userDO.getNickname())
                .avatar(userDO.getAvatar())
                .phone(userDO.getPhone())
                .email(userDO.getEmail())
                .gender(userDO.getGender())
                .address(userDO.getAddress())
                .role(userDO.getRole())
                .creditScore(userDO.getCreditScore())
                .certificationMaterials(userDO.getCertificationMaterials())
                .status(userDO.getStatus())
                .createTime(userDO.getCreateTime())
                .updateTime(userDO.getUpdateTime())
                .isDelete(userDO.getIsDelete())
                .password(null)
                .build();

        return LoginResponseDTO.builder()
                .token(jwtTokenUtil.generateToken(userDTO))
                .tokenType("Bearer")
                .expiresIn(6 * 60 * 60L)
                .userInfo(userDTO)
                .build();
    }

    /**
     * 用户注册。
     * 先通过布隆过滤器和数据库双重校验用户名是否已存在，再对密码进行 SM3 加密后完成注册。
     *
     * @param registerRequestDto 注册请求
     * @return 注册结果
     */
    @Override
    public String register(RegisterRequestDto registerRequestDto) {
        if (registerRequestDto == null
                || !StringUtils.hasText(registerRequestDto.getUsername())
                || !StringUtils.hasText(registerRequestDto.getPassword())
                || !StringUtils.hasText(registerRequestDto.getNickname())) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "用户名、密码或昵称不能为空");
        }

        String username = registerRequestDto.getUsername().trim();
        if (usernameBloomFilterUtil.mightContain(username) && existsUsername(username)) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "用户名已存在");
        }

        if (existsUsername(username)) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "用户名已存在");
        }

        LocalDateTime now = LocalDateTime.now();
        UserDO userDO = new UserDO();
        userDO.setUsername(username);
        userDO.setPassword(SmUtil.sm3(registerRequestDto.getPassword().trim()));
        userDO.setNickname(registerRequestDto.getNickname().trim());
        userDO.setPhone(registerRequestDto.getPhone());
        userDO.setEmail(registerRequestDto.getEmail());
        userDO.setRole(1);
        userDO.setCreditScore(100);
        userDO.setStatus(0);
        userDO.setIsDelete(0);
        userDO.setCreateTime(now);
        userDO.setUpdateTime(now);

        if (userMapper.insert(userDO) <= 0) {
            throw new UserLoginException(HttpCode.FAILED, "注册失败");
        }

        usernameBloomFilterUtil.addUsername(username);
        return "注册成功";
    }

    /**
     * 校验用户名是否已存在。
     *
     * @param username 用户名
     * @return 是否已存在
     */
    private boolean existsUsername(String username) {
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<UserDO>()
                .eq(UserDO::getUsername, username)
                .eq(UserDO::getIsDelete, 0)
                .last("limit 1");
        return userMapper.selectCount(wrapper) > 0;
    }
}
