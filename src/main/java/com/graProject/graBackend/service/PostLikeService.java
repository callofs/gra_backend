package com.graProject.graBackend.service;

import com.graProject.graBackend.dto.UserDTO;

/**
 * 点赞服务。
 */
public interface PostLikeService {

    /**
     * 点赞。
     *
     * @param loginUser 当前登录用户
     * @param likeType  点赞类型 1=贴文 2=评论
     * @param relateId  关联ID（贴文ID/评论ID）
     */
    void like(UserDTO loginUser, Integer likeType, Long relateId);

    /**
     * 取消点赞。
     *
     * @param loginUser 当前登录用户
     * @param likeType  点赞类型 1=贴文 2=评论
     * @param relateId  关联ID（贴文ID/评论ID）
     */
    void unlike(UserDTO loginUser, Integer likeType, Long relateId);

    /**
     * 查询当前用户是否已点赞。
     *
     * @param loginUser 当前登录用户
     * @param likeType  点赞类型 1=贴文 2=评论
     * @param relateId  关联ID（贴文ID/评论ID）
     * @return 是否已点赞
     */
    boolean hasLiked(UserDTO loginUser, Integer likeType, Long relateId);
}
