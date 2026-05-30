package com.graProject.graBackend.service;

import com.graProject.graBackend.dto.LoginRequestDTO;
import com.graProject.graBackend.dto.LoginResponseDTO;
import com.graProject.graBackend.dto.FileDownloadDTO;
import com.graProject.graBackend.dto.ExpertCertificationMaterialDTO;
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
     * @param certificationFile  专家认证材料文件（专家角色必传）
     * @return 注册结果
     */
    String register(RegisterRequestDto registerRequestDto, MultipartFile certificationFile);

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
     * 根据用户 ID 获取用户资料。
     *
     * @param loginUser 当前登录用户，可为空
     * @param userId    用户 ID
     * @return 用户资料；用户不存在时返回 null
     */
    UserDTO getUserProfileById(UserDTO loginUser, Long userId);

    /**
     * 获取专家用户列表。
     *
     * @param loginUser 当前登录用户，可为空
     * @param limit     获取数量限制，默认10
     * @return 专家用户列表
     */
    java.util.List<UserDTO> listExpertUsers(UserDTO loginUser, Integer limit);

    /**
     * 关注指定用户。
     *
     * @param loginUser  当前登录用户
     * @param followedId 被关注用户 ID
     */
    void followUser(UserDTO loginUser, Long followedId);

    /**
     * 取消关注指定用户。
     *
     * @param loginUser  当前登录用户
     * @param followedId 被取消关注用户 ID
     */
    void unfollowUser(UserDTO loginUser, Long followedId);

    /**
     * 获取当前登录用户关注的用户列表。
     *
     * @param loginUser 当前登录用户
     * @return 关注用户列表
     */
    java.util.List<UserDTO> listMyFollowedUsers(UserDTO loginUser);

    /**
     * 获取当前登录用户的粉丝列表。
     *
     * @param loginUser 当前登录用户
     * @return 粉丝用户列表
     */
    java.util.List<UserDTO> listMyFollowers(UserDTO loginUser);

    /**
     * 获取当前登录用户头像二进制数据。
     *
     * @param loginUser 当前登录用户
     * @return 头像二进制数据
     */
    byte[] getCurrentUserAvatar(UserDTO loginUser);

    /**
     * 获取当前登录用户的专家认证材料。
     *
     * @param loginUser 当前登录用户
     * @return 认证材料下载信息（未上传时返回 null）
     */
    FileDownloadDTO getCurrentUserCertificationMaterial(UserDTO loginUser);

    /**
     * 管理员获取指定用户的专家认证材料。
     *
     * @param loginUser 当前登录用户（必须为管理员）
     * @param userId    被查看的用户 ID
     * @return 认证材料下载信息（未上传时返回 null）
     */
    FileDownloadDTO getUserCertificationMaterialByAdmin(UserDTO loginUser, Long userId);

    /**
     * 管理员修改用户角色。
     *
     * @param loginUser 当前登录用户（必须为管理员）
     * @param userId    被修改的用户 ID
     * @param role      新角色：1=普通家长 2=认证专家 3=平台管理员
     */
    void updateUserRoleByAdmin(UserDTO loginUser, Long userId, Integer role);

    /**
     * 管理员获取专家认证材料列表。
     *
     * @param loginUser 当前登录用户（必须为管理员）
     * @return 专家认证材料列表
     */
    java.util.List<ExpertCertificationMaterialDTO> listExpertCertificationMaterials(UserDTO loginUser);
}
