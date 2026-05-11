package com.graProject.graBackend.service.impl;

import com.graProject.graBackend.dto.PostCommentCreateRequestDTO;
import com.graProject.graBackend.dto.PostCommentDTO;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.entity.ForumPostDO;
import com.graProject.graBackend.entity.PostCommentDO;
import com.graProject.graBackend.entity.UserDO;
import com.graProject.graBackend.mapper.ForumPostMapper;
import com.graProject.graBackend.mapper.PostCommentMapper;
import com.graProject.graBackend.mapper.UserMapper;
import com.graProject.graBackend.service.PostCommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 贴文评论/回复服务实现。
 */
@Service
public class PostCommentServiceImpl implements PostCommentService {

    private final PostCommentMapper postCommentMapper;

    private final ForumPostMapper forumPostMapper;

    private final UserMapper userMapper;

    public PostCommentServiceImpl(PostCommentMapper postCommentMapper, ForumPostMapper forumPostMapper,
            UserMapper userMapper) {
        this.postCommentMapper = postCommentMapper;
        this.forumPostMapper = forumPostMapper;
        this.userMapper = userMapper;
    }

    /**
     * 发表评论/回复。
     *
     * @param loginUser  当前登录用户
     * @param requestDTO 发布参数
     * @return 新增的评论
     */
    @Override
    public PostCommentDTO createComment(UserDTO loginUser, PostCommentCreateRequestDTO requestDTO) {
        if (loginUser == null || loginUser.getId() == null) {
            throw new IllegalArgumentException("登录状态无效");
        }
        if (requestDTO == null) {
            throw new IllegalArgumentException("评论参数不能为空");
        }
        if (requestDTO.getPostId() == null) {
            throw new IllegalArgumentException("贴文ID不能为空");
        }
        if (!StringUtils.hasText(requestDTO.getContent())) {
            throw new IllegalArgumentException("评论内容不能为空");
        }

        if (requestDTO.getIsAnonymous() != null && requestDTO.getIsAnonymous() == 1) {
            throw new IllegalArgumentException("评论不允许匿名");
        }

        ForumPostDO post = forumPostMapper.selectById(requestDTO.getPostId());
        if (post == null) {
            throw new IllegalArgumentException("贴文不存在");
        }

        if (requestDTO.getParentCommentId() != null) {
            PostCommentDO parent = postCommentMapper.selectById(requestDTO.getParentCommentId());
            if (parent == null) {
                throw new IllegalArgumentException("父评论不存在");
            }
            if (parent.getPostId() == null || !parent.getPostId().equals(requestDTO.getPostId())) {
                throw new IllegalArgumentException("父评论不属于该贴文");
            }
        }

        PostCommentDO record = new PostCommentDO();
        record.setPostId(requestDTO.getPostId());
        record.setUserId(loginUser.getId());
        record.setParentCommentId(requestDTO.getParentCommentId());
        record.setContent(requestDTO.getContent());
        record.setIsAnonymous(0);
        record.setLikeCount(0);
        record.setCreateTime(LocalDateTime.now());
        record.setIsDelete(0);

        postCommentMapper.insert(record);

        PostCommentDTO dto = new PostCommentDTO();
        BeanUtils.copyProperties(record, dto);
        return dto;
    }

    /**
     * 根据贴文 ID 获取一级评论列表。
     *
     * @param postId 贴文 ID
     * @return 一级评论列表
     */
    @Override
    public List<PostCommentDTO> listCommentsByPostId(Long postId) {
        if (postId == null) {
            return List.of();
        }

        List<PostCommentDO> records = postCommentMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PostCommentDO>()
                        .eq(PostCommentDO::getPostId, postId)
                        .and(w -> w.isNull(PostCommentDO::getParentCommentId)
                                .or()
                                .eq(PostCommentDO::getParentCommentId, 0L))
                        .orderByAsc(PostCommentDO::getCreateTime)
                        .orderByAsc(PostCommentDO::getId));

        if (records == null || records.isEmpty()) {
            return List.of();
        }

        Map<Long, UserDO> authorMap = resolveAuthorMap(records);
        List<PostCommentDTO> result = new ArrayList<>();
        for (PostCommentDO record : records) {
            if (record == null) {
                continue;
            }
            PostCommentDTO dto = new PostCommentDTO();
            BeanUtils.copyProperties(record, dto);

            UserDO author = authorMap.get(record.getUserId());
            if (author != null) {
                dto.setAuthorNickname(author.getNickname());
                dto.setAuthorAvatar(author.getAvatar());
            }
            result.add(dto);
        }
        return result;
    }

    /**
     * 根据父评论 ID 获取回复列表。
     *
     * @param parentCommentId 父评论 ID
     * @return 回复列表
     */
    @Override
    public List<PostCommentDTO> listRepliesByParentId(Long parentCommentId) {
        if (parentCommentId == null) {
            return List.of();
        }

        PostCommentDO parent = postCommentMapper.selectById(parentCommentId);
        if (parent == null) {
            return List.of();
        }

        List<PostCommentDO> records = postCommentMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PostCommentDO>()
                        .eq(PostCommentDO::getParentCommentId, parentCommentId)
                        .eq(PostCommentDO::getPostId, parent.getPostId())
                        .orderByAsc(PostCommentDO::getCreateTime)
                        .orderByAsc(PostCommentDO::getId));
        if (records == null || records.isEmpty()) {
            return List.of();
        }

        Map<Long, UserDO> authorMap = resolveAuthorMap(records);
        List<PostCommentDTO> result = new ArrayList<>();
        for (PostCommentDO record : records) {
            if (record == null) {
                continue;
            }
            PostCommentDTO dto = new PostCommentDTO();
            BeanUtils.copyProperties(record, dto);

            UserDO author = authorMap.get(record.getUserId());
            if (author != null) {
                dto.setAuthorNickname(author.getNickname());
                dto.setAuthorAvatar(author.getAvatar());
            }
            result.add(dto);
        }
        return result;
    }

    private Map<Long, UserDO> resolveAuthorMap(List<PostCommentDO> records) {
        Set<Long> userIds = records.stream()
                .filter(r -> r != null)
                .map(PostCommentDO::getUserId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        if (userIds.isEmpty()) {
            return Map.of();
        }

        List<UserDO> users = userMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserDO>()
                        .in(UserDO::getId, userIds));
        if (users == null || users.isEmpty()) {
            return Map.of();
        }
        return users.stream()
                .filter(u -> u != null && u.getId() != null)
                .collect(Collectors.toMap(UserDO::getId, Function.identity(), (a, b) -> a));
    }
}
