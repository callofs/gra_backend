package com.graProject.graBackend.service;

import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.entity.ForumPostDO;
import com.graProject.graBackend.dto.ForumPostDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Service;

/**
 * 论坛贴文相关方法
 */
@Service
public interface ForumPostService {
    /**
     * 发布论坛贴文。
     *
     * <p>
     * 接收贴文实体并持久化到数据库；必要字段为空时抛出参数异常。
     * </p>
     *
     * @param forumPostDO 论坛贴文
     * @return 发布结果提示
     */
    String uploadForumPost(ForumPostDO forumPostDO);

    /**
     * 获取贴文详情。
     *
     * <p>
     * 用于贴文详情页展示，通常需要登录后访问。
     * </p>
     *
     * @param postId    贴文 ID
     * @param loginUser 当前登录用户
     * @return 贴文详情；不存在时返回 null
     */
    ForumPostDTO getForumPostDetail(Long postId, UserDTO loginUser);

    /**
     * 分页查询贴文摘要列表。
     *
     * <p>
     * 用于贴文列表页展示，返回的 {@code ForumPostDTO.content} 为摘要文本（非完整 HTML）。
     * </p>
     *
     * @param page        页码（从 1 开始）
     * @param size        每页条数
     * @param sectionCode 板块编码（可选）
     * @param keyword     标题关键字（可选）
     * @param loginUser   当前登录用户（可为空）
     * @return 分页结果
     */
    IPage<ForumPostDTO> listForumPostSummaries(long page, long size, String sectionCode, String keyword,
            UserDTO loginUser);

    /**
     * 分页查询管理员可审核的贴文列表。
     *
     * <p>
     * 管理员可按贴文状态和标题关键字筛选所有贴文，并查看其审核状态。
     * </p>
     *
     * @param page    页码（从 1 开始）
     * @param size    每页条数
     * @param status  贴文状态（可选）
     * @param keyword 标题关键字（可选）
     * @return 分页结果
     */
    IPage<ForumPostDTO> listForumPostsForAudit(long page, long size, Integer status, String keyword);

    /**
     * 审核论坛贴文。
     *
     * @param postId 贴文 ID
     * @param status 审核后的状态 1=已发布 2=已驳回
     * @return 审核结果提示
     */
    String auditForumPost(Long postId, Integer status);

    /**
     * 分页查询当前登录用户发布的贴文审核状态。
     *
     * @param loginUser 当前登录用户
     * @param page      页码（从 1 开始）
     * @param size      每页条数
     * @return 分页结果
     */
    IPage<ForumPostDTO> listMyForumPosts(UserDTO loginUser, long page, long size);

    /**
     * 收藏贴文。
     *
     * @param loginUser 当前登录用户
     * @param postId    贴文 ID
     */
    void collectForumPost(UserDTO loginUser, Long postId);

    /**
     * 取消收藏贴文。
     *
     * @param loginUser 当前登录用户
     * @param postId    贴文 ID
     */
    void uncollectForumPost(UserDTO loginUser, Long postId);

    /**
     * 分页查询当前登录用户的收藏贴文列表。
     *
     * @param loginUser 当前登录用户
     * @param page      页码（从 1 开始）
     * @param size      每页条数
     * @return 分页结果
     */
    IPage<ForumPostDTO> listMyCollectedForumPosts(UserDTO loginUser, long page, long size);

    /**
     * 分页查询当前登录用户的浏览历史列表。
     *
     * @param loginUser 当前登录用户
     * @param page      页码（从 1 开始）
     * @param size      每页条数
     * @return 分页结果
     */
    IPage<ForumPostDTO> listMyBrowseHistory(UserDTO loginUser, long page, long size);
}
