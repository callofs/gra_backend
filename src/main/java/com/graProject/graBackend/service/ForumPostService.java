package com.graProject.graBackend.service;

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
     * @param postId 贴文 ID
     * @return 贴文详情；不存在时返回 null
     */
    ForumPostDTO getForumPostDetail(Long postId);

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
     * @return 分页结果
     */
    IPage<ForumPostDTO> listForumPostSummaries(long page, long size, String sectionCode, String keyword);
}
