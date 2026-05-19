package com.graProject.graBackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.entity.ForumPostDO;
import com.graProject.graBackend.entity.PostCommentDO;
import com.graProject.graBackend.entity.PostLikeDO;
import com.graProject.graBackend.mapper.ForumPostMapper;
import com.graProject.graBackend.mapper.PostCommentMapper;
import com.graProject.graBackend.mapper.PostLikeMapper;
import com.graProject.graBackend.service.PostLikeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 点赞服务实现。
 */
@Service
public class PostLikeServiceImpl implements PostLikeService {

    private final PostLikeMapper postLikeMapper;
    private final ForumPostMapper forumPostMapper;
    private final PostCommentMapper postCommentMapper;

    public PostLikeServiceImpl(PostLikeMapper postLikeMapper, ForumPostMapper forumPostMapper,
            PostCommentMapper postCommentMapper) {
        this.postLikeMapper = postLikeMapper;
        this.forumPostMapper = forumPostMapper;
        this.postCommentMapper = postCommentMapper;
    }

    /**
     * 点赞。
     *
     * @param loginUser 当前登录用户
     * @param likeType  点赞类型 1=贴文 2=评论
     * @param relateId  关联ID（贴文ID/评论ID）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void like(UserDTO loginUser, Integer likeType, Long relateId) {
        Long userId = requireLoginUserId(loginUser);
        validateTarget(likeType, relateId);

        LambdaQueryWrapper<PostLikeDO> existsWrapper = new LambdaQueryWrapper<PostLikeDO>()
                .eq(PostLikeDO::getLikeType, likeType)
                .eq(PostLikeDO::getRelateId, relateId)
                .eq(PostLikeDO::getUserId, userId);
        if (postLikeMapper.selectCount(existsWrapper) > 0) {
            return;
        }

        PostLikeDO record = new PostLikeDO();
        record.setLikeType(likeType);
        record.setRelateId(relateId);
        record.setUserId(userId);
        record.setCreateTime(LocalDateTime.now());
        postLikeMapper.insert(record);

        incLikeCount(likeType, relateId);
    }

    /**
     * 取消点赞。
     *
     * @param loginUser 当前登录用户
     * @param likeType  点赞类型 1=贴文 2=评论
     * @param relateId  关联ID（贴文ID/评论ID）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlike(UserDTO loginUser, Integer likeType, Long relateId) {
        Long userId = requireLoginUserId(loginUser);
        validateTarget(likeType, relateId);

        LambdaQueryWrapper<PostLikeDO> wrapper = new LambdaQueryWrapper<PostLikeDO>()
                .eq(PostLikeDO::getLikeType, likeType)
                .eq(PostLikeDO::getRelateId, relateId)
                .eq(PostLikeDO::getUserId, userId);
        int deleted = postLikeMapper.delete(wrapper);
        if (deleted <= 0) {
            return;
        }

        decLikeCount(likeType, relateId);
    }

    /**
     * 查询当前用户是否已点赞。
     *
     * @param loginUser 当前登录用户
     * @param likeType  点赞类型 1=贴文 2=评论
     * @param relateId  关联ID（贴文ID/评论ID）
     * @return 是否已点赞
     */
    @Override
    public boolean hasLiked(UserDTO loginUser, Integer likeType, Long relateId) {
        Long userId = requireLoginUserId(loginUser);
        if (likeType == null || relateId == null) {
            return false;
        }
        LambdaQueryWrapper<PostLikeDO> wrapper = new LambdaQueryWrapper<PostLikeDO>()
                .eq(PostLikeDO::getLikeType, likeType)
                .eq(PostLikeDO::getRelateId, relateId)
                .eq(PostLikeDO::getUserId, userId);
        return postLikeMapper.selectCount(wrapper) > 0;
    }

    private Long requireLoginUserId(UserDTO loginUser) {
        if (loginUser == null || loginUser.getId() == null) {
            throw new IllegalArgumentException("登录状态无效");
        }
        return loginUser.getId();
    }

    private void validateTarget(Integer likeType, Long relateId) {
        if (likeType == null || (likeType != 1 && likeType != 2)) {
            throw new IllegalArgumentException("点赞类型不正确");
        }
        if (relateId == null) {
            throw new IllegalArgumentException("关联ID不能为空");
        }

        if (likeType == 1) {
            ForumPostDO post = forumPostMapper.selectById(relateId);
            if (post == null) {
                throw new IllegalArgumentException("贴文不存在");
            }
        } else {
            PostCommentDO comment = postCommentMapper.selectById(relateId);
            if (comment == null) {
                throw new IllegalArgumentException("评论不存在");
            }
        }
    }

    private void incLikeCount(Integer likeType, Long relateId) {
        if (likeType == 1) {
            forumPostMapper.update(null,
                    new LambdaUpdateWrapper<ForumPostDO>()
                            .eq(ForumPostDO::getId, relateId)
                            .setSql("like_count = like_count + 1"));
            return;
        }
        postCommentMapper.update(null,
                new LambdaUpdateWrapper<PostCommentDO>()
                        .eq(PostCommentDO::getId, relateId)
                        .setSql("like_count = like_count + 1"));
    }

    private void decLikeCount(Integer likeType, Long relateId) {
        String decSql = "like_count = CASE WHEN like_count > 0 THEN like_count - 1 ELSE 0 END";
        if (likeType == 1) {
            forumPostMapper.update(null,
                    new LambdaUpdateWrapper<ForumPostDO>()
                            .eq(ForumPostDO::getId, relateId)
                            .setSql(decSql));
            return;
        }
        postCommentMapper.update(null,
                new LambdaUpdateWrapper<PostCommentDO>()
                        .eq(PostCommentDO::getId, relateId)
                        .setSql(decSql));
    }
}
