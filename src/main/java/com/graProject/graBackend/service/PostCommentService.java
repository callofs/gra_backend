package com.graProject.graBackend.service;

import com.graProject.graBackend.dto.PostCommentCreateRequestDTO;
import com.graProject.graBackend.dto.PostCommentDTO;
import com.graProject.graBackend.dto.UserDTO;

import java.util.List;

/**
 * 贴文评论/回复服务。
 */
public interface PostCommentService {

    /**
     * 发表评论/回复。
     *
     * @param loginUser  当前登录用户
     * @param requestDTO 发布参数
     * @return 新增的评论
     */
    PostCommentDTO createComment(UserDTO loginUser, PostCommentCreateRequestDTO requestDTO);

    /**
     * 根据贴文 ID 获取一级评论列表。
     *
     * @param postId 贴文 ID
     * @return 一级评论列表（parentCommentId 为空的记录）
     */
    List<PostCommentDTO> listCommentsByPostId(Long postId);

    /**
     * 根据父评论 ID 获取回复列表。
     *
     * @param parentCommentId 父评论 ID
     * @return 回复列表
     */
    List<PostCommentDTO> listRepliesByParentId(Long parentCommentId);
}
