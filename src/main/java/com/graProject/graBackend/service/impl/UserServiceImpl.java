package com.graProject.graBackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.hutool.crypto.SmUtil;
import com.graProject.graBackend.common.exception.User.UserLoginException;
import com.graProject.graBackend.common.result.HttpCode;
import com.graProject.graBackend.common.utils.AliyunOssUtil;
import com.graProject.graBackend.common.utils.JwtTokenUtil;
import com.graProject.graBackend.common.utils.UsernameBloomFilterUtil;
import com.graProject.graBackend.common.utils.UploadFileUtil;
import com.graProject.graBackend.dto.LoginRequestDTO;
import com.graProject.graBackend.dto.LoginResponseDTO;
import com.graProject.graBackend.dto.FileDownloadDTO;
import com.graProject.graBackend.dto.ExpertCertificationMaterialDTO;
import com.graProject.graBackend.dto.RegisterRequestDto;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.entity.UserDO;
import com.graProject.graBackend.mapper.UserMapper;
import com.graProject.graBackend.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

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
     * 用户关注 Mapper。
     */
    private final com.graProject.graBackend.mapper.UserFollowMapper userFollowMapper;

    /**
     * 用户名布隆过滤器工具。
     */
    private final UsernameBloomFilterUtil usernameBloomFilterUtil;

    /**
     * JWT 工具类。
     */
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * 阿里云 OSS 工具。
     */
    private final AliyunOssUtil aliyunOssUtil;

    /**
     * 构造方法。
     *
     * @param userMapper              用户 Mapper
     * @param usernameBloomFilterUtil 用户名布隆过滤器工具
     * @param jwtTokenUtil            JWT 工具类
     */
    public UserServiceImpl(UserMapper userMapper,
            com.graProject.graBackend.mapper.UserFollowMapper userFollowMapper,
            UsernameBloomFilterUtil usernameBloomFilterUtil,
            JwtTokenUtil jwtTokenUtil, AliyunOssUtil aliyunOssUtil) {
        this.userMapper = userMapper;
        this.userFollowMapper = userFollowMapper;
        this.usernameBloomFilterUtil = usernameBloomFilterUtil;
        this.jwtTokenUtil = jwtTokenUtil;
        this.aliyunOssUtil = aliyunOssUtil;
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
                .certificationMaterials(null)
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
    public String register(RegisterRequestDto registerRequestDto, MultipartFile certificationFile) {
        if (registerRequestDto == null
                || !StringUtils.hasText(registerRequestDto.getUsername())
                || !StringUtils.hasText(registerRequestDto.getPassword())
                || !StringUtils.hasText(registerRequestDto.getNickname())
                || !StringUtils.hasText(registerRequestDto.getPhone())) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "账号、昵称、密码或电话不能为空");
        }

        String username = registerRequestDto.getUsername().trim();
        if (usernameBloomFilterUtil.mightContain(username) && existsUsername(username)) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "用户名已存在");
        }

        if (existsUsername(username)) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "用户名已存在");
        }

        Integer role = registerRequestDto.getRole();
        if (role == null) {
            role = 1;
        }
        if (role != 1 && role != 2 && role != 3) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "角色参数不合法");
        }

        String certificationObjectKey = null;
        byte[] certificationBytes = null;
        if (role == 2) {
            if (certificationFile == null || certificationFile.isEmpty()) {
                throw new UserLoginException(HttpCode.BAD_REQUEST, "专家角色必须上传认证材料");
            }
            try {
                certificationBytes = UploadFileUtil.readBytes(
                        certificationFile,
                        Set.of("pdf", "doc", "docx"),
                        5 * 1024 * 1024L);
            } catch (IllegalArgumentException e) {
                throw new UserLoginException(HttpCode.BAD_REQUEST, e.getMessage());
            } catch (IllegalStateException e) {
                throw new UserLoginException(HttpCode.FAILED, e.getMessage());
            }
        }

        LocalDateTime now = LocalDateTime.now();
        UserDO userDO = new UserDO();
        userDO.setUsername(username);
        userDO.setPassword(SmUtil.sm3(registerRequestDto.getPassword().trim()));
        userDO.setNickname(registerRequestDto.getNickname().trim());
        userDO.setPhone(registerRequestDto.getPhone().trim());
        userDO.setEmail(registerRequestDto.getEmail());
        userDO.setRole(role);
        userDO.setCreditScore(100);
        userDO.setCertificationMaterials(null);
        userDO.setStatus(0);
        userDO.setIsDelete(0);
        userDO.setCreateTime(now);
        userDO.setUpdateTime(now);

        if (userMapper.insert(userDO) <= 0) {
            throw new UserLoginException(HttpCode.FAILED, "注册失败");
        }

        if (role == 2) {
            String ext = UploadFileUtil
                    .getExtensionLower(certificationFile == null ? null : certificationFile.getOriginalFilename());
            String dateFolder = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            certificationObjectKey = "certification/" + userDO.getId() + "/" + dateFolder + "/" + UUID.randomUUID()
                    + "." + ext;
            try {
                aliyunOssUtil.putObject(certificationObjectKey, certificationBytes);
            } catch (RuntimeException e) {
                userMapper.deleteById(userDO.getId());
                throw new UserLoginException(HttpCode.FAILED, "认证材料上传失败");
            }

            UserDO updateUser = new UserDO();
            updateUser.setId(userDO.getId());
            updateUser.setCertificationMaterials(certificationObjectKey);
            updateUser.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(updateUser);
        }

        usernameBloomFilterUtil.addUsername(username);
        return "注册成功";
    }

    /**
     * 获取当前登录用户的专家认证材料。
     *
     * @param loginUser 当前登录用户
     * @return 认证材料二进制数据（未上传时返回 null）
     */
    @Override
    public FileDownloadDTO getCurrentUserCertificationMaterial(UserDTO loginUser) {
        UserDO userDO = getCurrentUserEntity(loginUser);
        String objectKey = userDO.getCertificationMaterials();
        if (!StringUtils.hasText(objectKey)) {
            return null;
        }
        return buildFileDownload(objectKey);
    }

    /**
     * 管理员获取指定用户的专家认证材料。
     *
     * @param loginUser 当前登录用户（必须为管理员）
     * @param userId    被查看的用户 ID
     * @return 认证材料二进制数据（未上传时返回 null）
     */
    @Override
    public FileDownloadDTO getUserCertificationMaterialByAdmin(UserDTO loginUser, Long userId) {
        if (loginUser == null || loginUser.getId() == null) {
            throw new UserLoginException(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        if (loginUser.getRole() == null || loginUser.getRole() != 3) {
            throw new UserLoginException(HttpCode.FORBIDDEN, HttpCode.FORBIDDEN.getMessage());
        }
        if (userId == null) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "用户ID不能为空");
        }

        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<UserDO>()
                .eq(UserDO::getId, userId)
                .eq(UserDO::getIsDelete, 0)
                .last("limit 1");
        UserDO userDO = userMapper.selectOne(wrapper);
        if (userDO == null) {
            throw new UserLoginException(HttpCode.NOT_FOUND, "用户不存在");
        }

        String objectKey = userDO.getCertificationMaterials();
        if (!StringUtils.hasText(objectKey)) {
            return null;
        }
        return buildFileDownload(objectKey);
    }

    private FileDownloadDTO buildFileDownload(String objectKey) {
        String filename = getFilenameFromObjectKey(objectKey);
        String contentType = guessContentTypeByFilename(filename);
        byte[] bytes = aliyunOssUtil.getObjectBytes(objectKey);
        return FileDownloadDTO.builder()
                .bytes(bytes)
                .filename(filename)
                .contentType(contentType)
                .build();
    }

    private String getFilenameFromObjectKey(String objectKey) {
        if (!StringUtils.hasText(objectKey)) {
            return "file";
        }
        int slashIndex = objectKey.lastIndexOf('/');
        if (slashIndex >= 0 && slashIndex + 1 < objectKey.length()) {
            return objectKey.substring(slashIndex + 1);
        }
        return objectKey;
    }

    private String guessContentTypeByFilename(String filename) {
        if (!StringUtils.hasText(filename)) {
            return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        String lower = filename.toLowerCase();
        if (lower.endsWith(".pdf")) {
            return MediaType.APPLICATION_PDF_VALUE;
        }
        if (lower.endsWith(".doc")) {
            return "application/msword";
        }
        if (lower.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }
        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }

    /**
     * 修改当前用户头像。
     *
     * 接收图片文件并以二进制形式写入用户表的 avatar 字段。
     *
     * @param loginUser 当前登录用户
     * @param file      头像图片文件
     * @return 修改结果提示
     */
    @Override
    public String updateAvatar(UserDTO loginUser, MultipartFile file) {
        if (loginUser == null || loginUser.getId() == null) {
            throw new UserLoginException(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        if (file == null || file.isEmpty()) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "头像文件不能为空");
        }

        String contentType = file.getContentType();
        if (contentType == null || !StringUtils.hasText(contentType)) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "仅支持上传图片格式文件");
        }

        String lowerContentType = contentType.toLowerCase();
        if (!lowerContentType.startsWith("image/")) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "仅支持上传图片格式文件");
        }

        byte[] avatarBytes;
        try {
            avatarBytes = file.getBytes();
        } catch (java.io.IOException e) {
            throw new UserLoginException(HttpCode.FAILED, "头像读取失败");
        }

        UserDO updateUser = new UserDO();
        updateUser.setId(loginUser.getId());
        updateUser.setAvatar(avatarBytes);
        updateUser.setUpdateTime(LocalDateTime.now());

        if (userMapper.updateById(updateUser) <= 0) {
            throw new UserLoginException(HttpCode.FAILED, "头像更新失败");
        }
        return "头像更新成功";
    }

    /**
     * 获取当前登录用户的完整资料。
     *
     * 根据当前登录用户 ID 从数据库读取最新用户信息并返回。
     *
     * @param loginUser 当前登录用户
     * @return 当前登录用户完整资料
     */
    @Override
    public UserDTO getCurrentUserProfile(UserDTO loginUser) {
        UserDO userDO = getCurrentUserEntity(loginUser);
        return buildUserProfileDTO(userDO);
    }

    /**
     * 根据用户 ID 获取用户资料。
     *
     * @param loginUser 当前登录用户，可为空
     * @param userId    用户 ID
     * @return 用户资料；不存在时返回 null
     */
    @Override
    public UserDTO getUserProfileById(UserDTO loginUser, Long userId) {
        if (userId == null) {
            return null;
        }

        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<UserDO>()
                .eq(UserDO::getId, userId)
                .eq(UserDO::getIsDelete, 0)
                .last("limit 1");
        UserDO userDO = userMapper.selectOne(wrapper);
        if (userDO == null) {
            return null;
        }

        UserDTO userDTO = buildUserProfileDTO(userDO);
        Long loginUserId = loginUser == null ? null : loginUser.getId();
        userDTO.setFollowed(isFollowedByUser(loginUserId, userId));
        return userDTO;
    }

    /**
     * 获取专家用户列表。
     *
     * @param loginUser 当前登录用户，可为空
     * @param limit     获取数量限制，默认10
     * @return 专家用户列表
     */
    @Override
    public java.util.List<UserDTO> listExpertUsers(UserDTO loginUser, Integer limit) {
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<UserDO>()
                .eq(UserDO::getRole, 2)
                .eq(UserDO::getIsDelete, 0)
                .orderByDesc(UserDO::getCreateTime)
                .orderByDesc(UserDO::getId)
                .last("limit " + (limit != null && limit > 0 ? limit : 10));
        java.util.List<UserDO> userDOS = userMapper.selectList(wrapper);
        if (userDOS == null || userDOS.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        Long loginUserId = loginUser == null ? null : loginUser.getId();
        java.util.List<UserDTO> result = new java.util.ArrayList<>();
        for (UserDO userDO : userDOS) {
            if (userDO == null || userDO.getId() == null) {
                continue;
            }
            UserDTO userDTO = buildUserProfileDTO(userDO);
            userDTO.setFollowed(isFollowedByUser(loginUserId, userDO.getId()));
            result.add(userDTO);
        }
        return result;
    }

    /**
     * 关注指定用户。
     *
     * @param loginUser  当前登录用户
     * @param followedId 被关注用户 ID
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public void followUser(UserDTO loginUser, Long followedId) {
        Long followerId = requireLoginUserId(loginUser);
        validateFollowTarget(followerId, followedId);

        LambdaQueryWrapper<com.graProject.graBackend.entity.UserFollowDO> existsWrapper = new LambdaQueryWrapper<com.graProject.graBackend.entity.UserFollowDO>()
                .eq(com.graProject.graBackend.entity.UserFollowDO::getFollowerId, followerId)
                .eq(com.graProject.graBackend.entity.UserFollowDO::getFollowedId, followedId);
        if (userFollowMapper.selectCount(existsWrapper) > 0) {
            return;
        }

        com.graProject.graBackend.entity.UserFollowDO record = new com.graProject.graBackend.entity.UserFollowDO();
        record.setFollowerId(followerId);
        record.setFollowedId(followedId);
        record.setCreateTime(LocalDateTime.now());
        userFollowMapper.insert(record);
    }

    /**
     * 取消关注指定用户。
     *
     * @param loginUser  当前登录用户
     * @param followedId 被取消关注用户 ID
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public void unfollowUser(UserDTO loginUser, Long followedId) {
        Long followerId = requireLoginUserId(loginUser);
        if (followedId == null) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "被取消关注用户ID不能为空");
        }
        LambdaQueryWrapper<com.graProject.graBackend.entity.UserFollowDO> wrapper = new LambdaQueryWrapper<com.graProject.graBackend.entity.UserFollowDO>()
                .eq(com.graProject.graBackend.entity.UserFollowDO::getFollowerId, followerId)
                .eq(com.graProject.graBackend.entity.UserFollowDO::getFollowedId, followedId);
        userFollowMapper.delete(wrapper);
    }

    /**
     * 获取当前登录用户关注的用户列表。
     *
     * @param loginUser 当前登录用户
     * @return 关注用户列表
     */
    @Override
    public java.util.List<UserDTO> listMyFollowedUsers(UserDTO loginUser) {
        Long followerId = requireLoginUserId(loginUser);
        LambdaQueryWrapper<com.graProject.graBackend.entity.UserFollowDO> wrapper = new LambdaQueryWrapper<com.graProject.graBackend.entity.UserFollowDO>()
                .eq(com.graProject.graBackend.entity.UserFollowDO::getFollowerId, followerId)
                .orderByDesc(com.graProject.graBackend.entity.UserFollowDO::getCreateTime);
        java.util.List<com.graProject.graBackend.entity.UserFollowDO> followRecords = userFollowMapper
                .selectList(wrapper);
        if (followRecords == null || followRecords.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        java.util.List<Long> followedIds = followRecords.stream()
                .map(com.graProject.graBackend.entity.UserFollowDO::getFollowedId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();
        if (followedIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        LambdaQueryWrapper<UserDO> userWrapper = new LambdaQueryWrapper<UserDO>()
                .in(UserDO::getId, followedIds)
                .eq(UserDO::getIsDelete, 0);
        java.util.List<UserDO> userDOS = userMapper.selectList(userWrapper);
        if (userDOS == null || userDOS.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        java.util.Map<Long, UserDO> userMap = userDOS.stream()
                .filter(user -> user != null && user.getId() != null)
                .collect(java.util.stream.Collectors.toMap(UserDO::getId, user -> user, (left, right) -> left));

        java.util.List<UserDTO> result = new java.util.ArrayList<>();
        for (Long followedId : followedIds) {
            UserDO userDO = userMap.get(followedId);
            if (userDO == null) {
                continue;
            }
            result.add(buildUserProfileDTO(userDO));
        }
        return result;
    }

    /**
     * 获取当前登录用户的粉丝列表。
     *
     * @param loginUser 当前登录用户
     * @return 粉丝用户列表
     */
    @Override
    public java.util.List<UserDTO> listMyFollowers(UserDTO loginUser) {
        Long followedId = requireLoginUserId(loginUser);
        LambdaQueryWrapper<com.graProject.graBackend.entity.UserFollowDO> wrapper = new LambdaQueryWrapper<com.graProject.graBackend.entity.UserFollowDO>()
                .eq(com.graProject.graBackend.entity.UserFollowDO::getFollowedId, followedId)
                .orderByDesc(com.graProject.graBackend.entity.UserFollowDO::getCreateTime);
        java.util.List<com.graProject.graBackend.entity.UserFollowDO> followerRecords = userFollowMapper
                .selectList(wrapper);
        if (followerRecords == null || followerRecords.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        java.util.List<Long> followerIds = followerRecords.stream()
                .map(com.graProject.graBackend.entity.UserFollowDO::getFollowerId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();
        if (followerIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        LambdaQueryWrapper<UserDO> userWrapper = new LambdaQueryWrapper<UserDO>()
                .in(UserDO::getId, followerIds)
                .eq(UserDO::getIsDelete, 0);
        java.util.List<UserDO> userDOS = userMapper.selectList(userWrapper);
        if (userDOS == null || userDOS.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        java.util.Map<Long, UserDO> userMap = userDOS.stream()
                .filter(user -> user != null && user.getId() != null)
                .collect(java.util.stream.Collectors.toMap(UserDO::getId, user -> user, (left, right) -> left));

        java.util.List<UserDTO> result = new java.util.ArrayList<>();
        for (Long followerId : followerIds) {
            UserDO userDO = userMap.get(followerId);
            if (userDO == null) {
                continue;
            }
            result.add(buildUserProfileDTO(userDO));
        }
        return result;
    }

    /**
     * 获取当前登录用户头像二进制数据。
     *
     * @param loginUser 当前登录用户
     * @return 头像二进制数据
     */
    @Override
    public byte[] getCurrentUserAvatar(UserDTO loginUser) {
        UserDO userDO = getCurrentUserEntity(loginUser);
        return userDO.getAvatar();
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

    /**
     * 获取当前登录用户 ID。
     *
     * @param loginUser 当前登录用户
     * @return 当前登录用户 ID
     */
    private Long requireLoginUserId(UserDTO loginUser) {
        if (loginUser == null || loginUser.getId() == null) {
            throw new UserLoginException(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        return loginUser.getId();
    }

    /**
     * 校验关注目标是否合法。
     *
     * @param followerId 关注者 ID
     * @param followedId 被关注者 ID
     */
    private void validateFollowTarget(Long followerId, Long followedId) {
        if (followedId == null) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "被关注用户ID不能为空");
        }
        if (followerId.equals(followedId)) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "不能关注自己");
        }
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<UserDO>()
                .eq(UserDO::getId, followedId)
                .eq(UserDO::getIsDelete, 0)
                .last("limit 1");
        UserDO userDO = userMapper.selectOne(wrapper);
        if (userDO == null) {
            throw new UserLoginException(HttpCode.NOT_FOUND, "用户不存在");
        }
    }

    /**
     * 根据当前登录用户信息读取数据库中的最新用户记录。
     *
     * @param loginUser 当前登录用户
     * @return 用户实体
     */
    private UserDO getCurrentUserEntity(UserDTO loginUser) {
        if (loginUser == null || loginUser.getId() == null) {
            throw new UserLoginException(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }

        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<UserDO>()
                .eq(UserDO::getId, loginUser.getId())
                .eq(UserDO::getIsDelete, 0)
                .last("limit 1");
        UserDO userDO = userMapper.selectOne(wrapper);
        if (userDO == null) {
            throw new UserLoginException(HttpCode.NOT_FOUND, "用户不存在");
        }
        return userDO;
    }

    /**
     * 将用户实体转换为用户资料 DTO。
     *
     * @param userDO 用户实体
     * @return 用户资料 DTO
     */
    private UserDTO buildUserDTO(UserDO userDO) {
        return UserDTO.builder()
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
    }

    /**
     * 构建用于对外返回的用户资料 DTO。
     *
     * @param userDO 用户实体
     * @return 用户资料 DTO
     */
    private UserDTO buildUserProfileDTO(UserDO userDO) {
        UserDTO userDTO = buildUserDTO(userDO);
        // userDTO.setAvatar(null);
        userDTO.setCertificationMaterials(null);
        userDTO.setFollowCount(countFollowByFollowerId(userDO == null ? null : userDO.getId()));
        userDTO.setFollowerCount(countFollowByFollowedId(userDO == null ? null : userDO.getId()));
        return userDTO;
    }

    /**
     * 统计指定用户关注数。
     *
     * @param followerId 关注者 ID
     * @return 关注数
     */
    private Integer countFollowByFollowerId(Long followerId) {
        if (followerId == null) {
            return 0;
        }
        LambdaQueryWrapper<com.graProject.graBackend.entity.UserFollowDO> wrapper = new LambdaQueryWrapper<com.graProject.graBackend.entity.UserFollowDO>()
                .eq(com.graProject.graBackend.entity.UserFollowDO::getFollowerId, followerId);
        return Math.toIntExact(userFollowMapper.selectCount(wrapper));
    }

    /**
     * 统计指定用户粉丝数。
     *
     * @param followedId 被关注者 ID
     * @return 粉丝数
     */
    private Integer countFollowByFollowedId(Long followedId) {
        if (followedId == null) {
            return 0;
        }
        LambdaQueryWrapper<com.graProject.graBackend.entity.UserFollowDO> wrapper = new LambdaQueryWrapper<com.graProject.graBackend.entity.UserFollowDO>()
                .eq(com.graProject.graBackend.entity.UserFollowDO::getFollowedId, followedId);
        return Math.toIntExact(userFollowMapper.selectCount(wrapper));
    }

    /**
     * 判断当前登录用户是否已关注指定用户。
     *
     * @param loginUserId  当前登录用户 ID
     * @param targetUserId 目标用户 ID
     * @return 是否已关注
     */
    private boolean isFollowedByUser(Long loginUserId, Long targetUserId) {
        if (loginUserId == null || targetUserId == null || loginUserId.equals(targetUserId)) {
            return false;
        }
        LambdaQueryWrapper<com.graProject.graBackend.entity.UserFollowDO> wrapper = new LambdaQueryWrapper<com.graProject.graBackend.entity.UserFollowDO>()
                .eq(com.graProject.graBackend.entity.UserFollowDO::getFollowerId, loginUserId)
                .eq(com.graProject.graBackend.entity.UserFollowDO::getFollowedId, targetUserId);
        return userFollowMapper.selectCount(wrapper) > 0;
    }

    /**
     * 管理员修改用户角色。
     *
     * @param loginUser 当前登录用户（必须为管理员）
     * @param userId    被修改的用户 ID
     * @param role      新角色：1=普通家长 2=认证专家 3=平台管理员
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public void updateUserRoleByAdmin(UserDTO loginUser, Long userId, Integer role) {
        if (loginUser == null || loginUser.getId() == null) {
            throw new UserLoginException(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        if (loginUser.getRole() == null || loginUser.getRole() != 3) {
            throw new UserLoginException(HttpCode.FORBIDDEN, HttpCode.FORBIDDEN.getMessage());
        }
        if (userId == null) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "用户ID不能为空");
        }
        if (role == null || (role != 1 && role != 2 && role != 3)) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "角色参数不合法");
        }

        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<UserDO>()
                .eq(UserDO::getId, userId)
                .eq(UserDO::getIsDelete, 0)
                .last("limit 1");
        UserDO userDO = userMapper.selectOne(wrapper);
        if (userDO == null) {
            throw new UserLoginException(HttpCode.NOT_FOUND, "用户不存在");
        }

        UserDO updateUser = new UserDO();
        updateUser.setId(userId);
        updateUser.setRole(role);
        updateUser.setUpdateTime(LocalDateTime.now());
        if (userMapper.updateById(updateUser) <= 0) {
            throw new UserLoginException(HttpCode.FAILED, "角色修改失败");
        }
    }

    /**
     * 管理员获取专家认证材料列表。
     *
     * @param loginUser 当前登录用户（必须为管理员）
     * @return 专家认证材料列表
     */
    @Override
    public java.util.List<ExpertCertificationMaterialDTO> listExpertCertificationMaterials(UserDTO loginUser) {
        if (loginUser == null || loginUser.getId() == null) {
            throw new UserLoginException(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        if (loginUser.getRole() == null || loginUser.getRole() != 3) {
            throw new UserLoginException(HttpCode.FORBIDDEN, HttpCode.FORBIDDEN.getMessage());
        }

        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<UserDO>()
                .eq(UserDO::getIsDelete, 0)
                .isNotNull(UserDO::getCertificationMaterials)
                .orderByDesc(UserDO::getCreateTime)
                .orderByDesc(UserDO::getId);
        java.util.List<UserDO> expertUsers = userMapper.selectList(wrapper);
        if (expertUsers == null || expertUsers.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        java.util.List<ExpertCertificationMaterialDTO> result = new java.util.ArrayList<>();
        for (UserDO userDO : expertUsers) {
            if (userDO == null || userDO.getId() == null) {
                continue;
            }
            ExpertCertificationMaterialDTO dto = ExpertCertificationMaterialDTO.builder()
                    .userId(userDO.getId())
                    .username(userDO.getUsername())
                    .nickname(userDO.getNickname())
                    .certificationMaterials(userDO.getCertificationMaterials())
                    .createTime(userDO.getCreateTime())
                    .updateTime(userDO.getUpdateTime())
                    .build();
            result.add(dto);
        }
        return result;
    }
}
