package com.graProject.graBackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graProject.graBackend.common.result.HttpCode;
import com.graProject.graBackend.common.exception.User.UserLoginException;
import com.graProject.graBackend.common.utils.RichTextUtil;
import com.graProject.graBackend.dto.ForumPostDTO;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.entity.DictDO;
import com.graProject.graBackend.entity.ForumPostDO;
import com.graProject.graBackend.entity.UserDO;
import com.graProject.graBackend.mapper.DictMapper;
import com.graProject.graBackend.mapper.ForumPostMapper;
import com.graProject.graBackend.mapper.UserMapper;
import com.graProject.graBackend.service.ForumPostService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 论坛贴文相关业务实现类。
 *
 * <p>
 * 包含：贴文发布、贴文详情获取、贴文列表分页查询（摘要）。
 * </p>
 */
@Service
public class ForumPostServiceImpl implements ForumPostService {

    /**
     * 论坛贴文 Mapper。
     */
    private final ForumPostMapper forumPostMapper;

    /**
     * 系统字典 Mapper，用于将板块编码映射为板块名称。
     */
    private final DictMapper dictMapper;

    /**
     * 用户表 Mapper，用于补全贴文作者信息。
     */
    private final UserMapper userMapper;

    /**
     * 贴文收藏 Mapper。
     */
    private final com.graProject.graBackend.mapper.PostCollectionMapper postCollectionMapper;

    /**
     * 用户浏览记录 Mapper。
     */
    private final com.graProject.graBackend.mapper.UserBrowseMapper userBrowseMapper;

    /**
     * 构造方法。
     *
     * @param forumPostMapper 贴文 Mapper
     * @param dictMapper      字典 Mapper
     */
    public ForumPostServiceImpl(ForumPostMapper forumPostMapper, DictMapper dictMapper, UserMapper userMapper,
            com.graProject.graBackend.mapper.PostCollectionMapper postCollectionMapper,
            com.graProject.graBackend.mapper.UserBrowseMapper userBrowseMapper) {
        this.forumPostMapper = forumPostMapper;
        this.dictMapper = dictMapper;
        this.userMapper = userMapper;
        this.postCollectionMapper = postCollectionMapper;
        this.userBrowseMapper = userBrowseMapper;
    }

    /**
     * 发布贴文。
     *
     * <p>
     * 对贴文的必要字段做校验，并为部分字段填充默认值后写入数据库。
     * 默认状态为待审核（status=0）。
     * </p>
     *
     * @param forumPostDO 贴文数据
     * @return 发布结果提示
     */
    @Override
    public String uploadForumPost(ForumPostDO forumPostDO) {
        if (forumPostDO == null) {
            throw new IllegalArgumentException("贴文参数不能为空");
        }
        if (forumPostDO.getUserId() == null) {
            throw new IllegalArgumentException("发布者不能为空");
        }
        if (!StringUtils.hasText(forumPostDO.getSectionCode())) {
            throw new IllegalArgumentException("板块不能为空");
        }
        if (!StringUtils.hasText(forumPostDO.getTitle())) {
            throw new IllegalArgumentException("标题不能为空");
        }
        if (!StringUtils.hasText(forumPostDO.getContent())) {
            throw new IllegalArgumentException("正文不能为空");
        }

        LocalDateTime now = LocalDateTime.now();
        if (forumPostDO.getIsAnonymous() == null) {
            forumPostDO.setIsAnonymous(0);
        }
        if (forumPostDO.getIsEssence() == null) {
            forumPostDO.setIsEssence(0);
        }
        if (forumPostDO.getViewCount() == null) {
            forumPostDO.setViewCount(0);
        }
        if (forumPostDO.getLikeCount() == null) {
            forumPostDO.setLikeCount(0);
        }
        if (forumPostDO.getCommentCount() == null) {
            forumPostDO.setCommentCount(0);
        }
        if (forumPostDO.getCollectCount() == null) {
            forumPostDO.setCollectCount(0);
        }
        if (forumPostDO.getStatus() == null) {
            forumPostDO.setStatus(0);
        }
        if (forumPostDO.getCreateTime() == null) {
            forumPostDO.setCreateTime(now);
        }
        forumPostDO.setUpdateTime(now);
        if (forumPostDO.getIsDelete() == null) {
            forumPostDO.setIsDelete(0);
        }

        forumPostMapper.insert(forumPostDO);
        return "发布成功";
    }

    /**
     * 获取贴文详情。
     *
     * @param postId    贴文 ID
     * @param loginUser 当前登录用户
     * @return 贴文详情 DTO；不存在时返回 null
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public ForumPostDTO getForumPostDetail(Long postId, UserDTO loginUser) {
        ForumPostDO forumPostDO = selectPostById(postId);
        if (forumPostDO == null) {
            return null;
        }
        Long userId = validateLoginUser(loginUser);
        recordBrowseHistory(userId, postId);
        ForumPostDTO dto = new ForumPostDTO();
        BeanUtils.copyProperties(forumPostDO, dto);
        dto.setCollected(isCollectedByUser(userId, postId));

        if (forumPostDO.getIsAnonymous() == null || forumPostDO.getIsAnonymous() == 0) {
            fillAuthor(dto, forumPostDO.getUserId());
        }
        return dto;
    }

    /**
     * 分页查询贴文摘要列表。
     *
     * <p>
     * 仅查询已发布（status=1）的贴文，并将正文 HTML 转换为摘要文本返回。
     * </p>
     *
     * @param page        页码（从 1 开始）
     * @param size        每页条数（服务端会做上限保护）
     * @param sectionCode 板块编码（可选）
     * @param keyword     标题关键字（可选）
     * @param loginUser   当前登录用户（可为空）
     * @return 分页摘要结果
     */
    @Override
    public IPage<ForumPostDTO> listForumPostSummaries(long page, long size, String sectionCode, String keyword,
            UserDTO loginUser) {
        long current = Math.max(1, page);
        long pageSize = Math.min(Math.max(1, size), 50);

        LambdaQueryWrapper<ForumPostDO> wrapper = new LambdaQueryWrapper<ForumPostDO>()
                .eq(ForumPostDO::getStatus, 1)
                .orderByDesc(ForumPostDO::getCreateTime);

        if (StringUtils.hasText(sectionCode)) {
            wrapper.eq(ForumPostDO::getSectionCode, sectionCode);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(ForumPostDO::getTitle, keyword);
        }

        IPage<ForumPostDO> doPage = forumPostMapper.selectPage(new Page<>(current, pageSize), wrapper);
        return buildForumPostPage(doPage, true, loginUser == null ? null : loginUser.getId());
    }

    /**
     * 根据贴文 ID 顺序构建分页结果。
     *
     * @param current     当前页码
     * @param size        分页大小
     * @param total       总记录数
     * @param postIds     贴文 ID 列表
     * @param useSummary  是否将正文转换为摘要
     * @param loginUserId 当前登录用户 ID（可为空）
     * @return DTO 分页结果
     */
    private IPage<ForumPostDTO> buildForumPostPageFromPostIds(long current, long size, long total, List<Long> postIds,
            boolean useSummary, Long loginUserId) {
        List<ForumPostDO> forumPosts = listPostsByIdsInOrder(postIds);
        Page<ForumPostDTO> dtoPage = new Page<>(current, size, total);
        if (forumPosts.isEmpty()) {
            dtoPage.setRecords(java.util.Collections.emptyList());
            return dtoPage;
        }
        Page<ForumPostDO> doPage = new Page<>(current, size, total);
        doPage.setRecords(forumPosts);
        IPage<ForumPostDTO> result = buildForumPostPage(doPage, useSummary, loginUserId);
        dtoPage.setRecords(result.getRecords());
        return dtoPage;
    }

    /**
     * 分页查询管理员可审核的贴文列表。
     *
     * @param page    页码（从 1 开始）
     * @param size    每页条数
     * @param status  贴文状态（可选）
     * @param keyword 标题关键字（可选）
     * @return 分页结果
     */
    @Override
    public IPage<ForumPostDTO> listForumPostsForAudit(long page, long size, Integer status, String keyword) {
        long current = Math.max(1, page);
        long pageSize = Math.min(Math.max(1, size), 50);

        LambdaQueryWrapper<ForumPostDO> wrapper = new LambdaQueryWrapper<ForumPostDO>()
                .orderByDesc(ForumPostDO::getCreateTime)
                .orderByDesc(ForumPostDO::getId);
        if (status != null) {
            wrapper.eq(ForumPostDO::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(ForumPostDO::getTitle, keyword.trim());
        }

        IPage<ForumPostDO> doPage = forumPostMapper.selectPage(new Page<>(current, pageSize), wrapper);
        return buildForumPostPage(doPage, false, null);
    }

    /**
     * 审核论坛贴文。
     *
     * @param postId 贴文 ID
     * @param status 审核后的状态 1=已发布 2=已驳回
     * @return 审核结果提示
     */
    @Override
    public String auditForumPost(Long postId, Integer status) {
        if (postId == null) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "贴文ID不能为空");
        }
        if (status == null || (status != 1 && status != 2)) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "审核状态不合法");
        }
        ForumPostDO forumPostDO = selectPostById(postId);
        if (forumPostDO == null) {
            throw new UserLoginException(HttpCode.NOT_FOUND, "贴文不存在");
        }
        forumPostDO.setStatus(status);
        forumPostDO.setUpdateTime(LocalDateTime.now());
        forumPostMapper.updateById(forumPostDO);
        return status == 1 ? "审核通过" : "审核驳回成功";
    }

    /**
     * 分页查询当前登录用户发布的贴文审核状态。
     *
     * @param loginUser 当前登录用户
     * @param page      页码（从 1 开始）
     * @param size      每页条数
     * @return 分页结果
     */
    @Override
    public IPage<ForumPostDTO> listMyForumPosts(UserDTO loginUser, long page, long size) {
        Long userId = validateLoginUser(loginUser);
        long current = Math.max(1, page);
        long pageSize = Math.min(Math.max(1, size), 50);

        LambdaQueryWrapper<ForumPostDO> wrapper = new LambdaQueryWrapper<ForumPostDO>()
                .eq(ForumPostDO::getUserId, userId)
                .orderByDesc(ForumPostDO::getCreateTime)
                .orderByDesc(ForumPostDO::getId);
        IPage<ForumPostDO> doPage = forumPostMapper.selectPage(new Page<>(current, pageSize), wrapper);
        return buildForumPostPage(doPage, true, userId);
    }

    /**
     * 收藏贴文。
     *
     * @param loginUser 当前登录用户
     * @param postId    贴文 ID
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public void collectForumPost(UserDTO loginUser, Long postId) {
        Long userId = validateLoginUser(loginUser);
        ForumPostDO forumPostDO = requirePublishedPost(postId);
        LambdaQueryWrapper<com.graProject.graBackend.entity.PostCollectionDO> existsWrapper = new LambdaQueryWrapper<com.graProject.graBackend.entity.PostCollectionDO>()
                .eq(com.graProject.graBackend.entity.PostCollectionDO::getUserId, userId)
                .eq(com.graProject.graBackend.entity.PostCollectionDO::getPostId, forumPostDO.getId());
        if (postCollectionMapper.selectCount(existsWrapper) > 0) {
            return;
        }
        com.graProject.graBackend.entity.PostCollectionDO collectionDO = new com.graProject.graBackend.entity.PostCollectionDO();
        collectionDO.setUserId(userId);
        collectionDO.setPostId(forumPostDO.getId());
        collectionDO.setCreateTime(LocalDateTime.now());
        postCollectionMapper.insert(collectionDO);
        forumPostMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<ForumPostDO>()
                        .eq(ForumPostDO::getId, forumPostDO.getId())
                        .setSql("collect_count = collect_count + 1"));
    }

    /**
     * 取消收藏贴文。
     *
     * @param loginUser 当前登录用户
     * @param postId    贴文 ID
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public void uncollectForumPost(UserDTO loginUser, Long postId) {
        Long userId = validateLoginUser(loginUser);
        if (postId == null) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "贴文ID不能为空");
        }
        LambdaQueryWrapper<com.graProject.graBackend.entity.PostCollectionDO> wrapper = new LambdaQueryWrapper<com.graProject.graBackend.entity.PostCollectionDO>()
                .eq(com.graProject.graBackend.entity.PostCollectionDO::getUserId, userId)
                .eq(com.graProject.graBackend.entity.PostCollectionDO::getPostId, postId);
        int deleted = postCollectionMapper.delete(wrapper);
        if (deleted <= 0) {
            return;
        }
        forumPostMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<ForumPostDO>()
                        .eq(ForumPostDO::getId, postId)
                        .setSql("collect_count = CASE WHEN collect_count > 0 THEN collect_count - 1 ELSE 0 END"));
    }

    /**
     * 分页查询当前登录用户的收藏贴文列表。
     *
     * @param loginUser 当前登录用户
     * @param page      页码（从 1 开始）
     * @param size      每页条数
     * @return 分页结果
     */
    @Override
    public IPage<ForumPostDTO> listMyCollectedForumPosts(UserDTO loginUser, long page, long size) {
        Long userId = validateLoginUser(loginUser);
        long current = Math.max(1, page);
        long pageSize = Math.min(Math.max(1, size), 50);
        LambdaQueryWrapper<com.graProject.graBackend.entity.PostCollectionDO> wrapper = new LambdaQueryWrapper<com.graProject.graBackend.entity.PostCollectionDO>()
                .eq(com.graProject.graBackend.entity.PostCollectionDO::getUserId, userId)
                .orderByDesc(com.graProject.graBackend.entity.PostCollectionDO::getCreateTime)
                .orderByDesc(com.graProject.graBackend.entity.PostCollectionDO::getId);
        IPage<com.graProject.graBackend.entity.PostCollectionDO> collectionPage = postCollectionMapper.selectPage(
                new Page<>(current, pageSize),
                wrapper);
        return buildForumPostPageFromPostIds(collectionPage.getCurrent(), collectionPage.getSize(),
                collectionPage.getTotal(),
                collectionPage.getRecords().stream().map(com.graProject.graBackend.entity.PostCollectionDO::getPostId)
                        .collect(Collectors.toList()),
                true, userId);
    }

    /**
     * 分页查询当前登录用户的浏览历史列表。
     *
     * @param loginUser 当前登录用户
     * @param page      页码（从 1 开始）
     * @param size      每页条数
     * @return 分页结果
     */
    @Override
    public IPage<ForumPostDTO> listMyBrowseHistory(UserDTO loginUser, long page, long size) {
        Long userId = validateLoginUser(loginUser);
        long current = Math.max(1, page);
        long pageSize = Math.min(Math.max(1, size), 50);
        LambdaQueryWrapper<com.graProject.graBackend.entity.UserBrowseDO> wrapper = new LambdaQueryWrapper<com.graProject.graBackend.entity.UserBrowseDO>()
                .eq(com.graProject.graBackend.entity.UserBrowseDO::getUserId, userId)
                .eq(com.graProject.graBackend.entity.UserBrowseDO::getBrowseType, 1)
                .orderByDesc(com.graProject.graBackend.entity.UserBrowseDO::getBrowseTime)
                .orderByDesc(com.graProject.graBackend.entity.UserBrowseDO::getId);
        IPage<com.graProject.graBackend.entity.UserBrowseDO> browsePage = userBrowseMapper
                .selectPage(new Page<>(current, pageSize), wrapper);
        return buildForumPostPageFromPostIds(browsePage.getCurrent(), browsePage.getSize(), browsePage.getTotal(),
                browsePage.getRecords().stream().map(com.graProject.graBackend.entity.UserBrowseDO::getRelateId)
                        .collect(Collectors.toList()),
                true, userId);
    }

    /**
     * 批量解析板块编码对应的板块名称。
     *
     * <p>
     * 从当前页贴文记录中提取所有 {@code sectionCode}，批量查询字典表 {@code t_dict}，并生成映射。
     * </p>
     *
     * @param records 当前页贴文记录
     * @return sectionCode -> sectionName 映射
     */
    private Map<String, String> resolveSectionNameMap(List<ForumPostDO> records) {
        Map<String, String> map = new HashMap<>();
        if (records == null || records.isEmpty()) {
            return map;
        }
        Set<String> codes = new java.util.HashSet<>();
        for (ForumPostDO record : records) {
            if (record != null && StringUtils.hasText(record.getSectionCode())) {
                codes.add(record.getSectionCode());
            }
        }
        if (codes.isEmpty()) {
            return map;
        }
        LambdaQueryWrapper<DictDO> wrapper = new LambdaQueryWrapper<DictDO>()
                .in(DictDO::getDictCode, codes);
        List<DictDO> dicts = dictMapper.selectList(wrapper);
        if (dicts == null) {
            return map;
        }
        for (DictDO dict : dicts) {
            if (dict == null || !StringUtils.hasText(dict.getDictCode())) {
                continue;
            }
            map.put(dict.getDictCode(), dict.getDictName());
        }
        return map;
    }

    /**
     * 批量解析贴文作者信息。
     *
     * <p>
     * 为避免 N+1 查询，提取当前页中所有非匿名贴的 userId 后批量查询用户表。
     * </p>
     *
     * @param records 当前页贴文记录
     * @return userId -> UserDO 映射
     */
    private Map<Long, UserDO> resolveAuthorMap(List<ForumPostDO> records) {
        if (records == null || records.isEmpty()) {
            return Map.of();
        }
        Set<Long> userIds = records.stream()
                .filter(r -> r != null)
                .filter(r -> r.getIsAnonymous() == null || r.getIsAnonymous() == 0)
                .map(ForumPostDO::getUserId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        if (userIds.isEmpty()) {
            return Map.of();
        }

        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<UserDO>()
                .in(UserDO::getId, userIds);
        List<UserDO> users = userMapper.selectList(wrapper);
        if (users == null || users.isEmpty()) {
            return Map.of();
        }
        return users.stream()
                .filter(u -> u != null && u.getId() != null)
                .collect(Collectors.toMap(UserDO::getId, Function.identity(), (a, b) -> a));
    }

    /**
     * 批量解析当前登录用户已收藏的贴文 ID 集合。
     *
     * @param loginUserId 当前登录用户 ID（可为空）
     * @param records     当前页贴文记录
     * @return 已收藏贴文 ID 集合
     */
    private Set<Long> resolveCollectedPostIds(Long loginUserId, List<ForumPostDO> records) {
        if (loginUserId == null || records == null || records.isEmpty()) {
            return java.util.Collections.emptySet();
        }
        Set<Long> postIds = records.stream()
                .filter(post -> post != null && post.getId() != null)
                .map(ForumPostDO::getId)
                .collect(Collectors.toSet());
        if (postIds.isEmpty()) {
            return java.util.Collections.emptySet();
        }
        LambdaQueryWrapper<com.graProject.graBackend.entity.PostCollectionDO> wrapper = new LambdaQueryWrapper<com.graProject.graBackend.entity.PostCollectionDO>()
                .eq(com.graProject.graBackend.entity.PostCollectionDO::getUserId, loginUserId)
                .in(com.graProject.graBackend.entity.PostCollectionDO::getPostId, postIds);
        List<com.graProject.graBackend.entity.PostCollectionDO> collections = postCollectionMapper.selectList(wrapper);
        if (collections == null || collections.isEmpty()) {
            return java.util.Collections.emptySet();
        }
        return collections.stream()
                .map(com.graProject.graBackend.entity.PostCollectionDO::getPostId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * 判断当前登录用户是否已收藏指定贴文。
     *
     * @param loginUserId 当前登录用户 ID
     * @param postId      贴文 ID
     * @return 是否已收藏
     */
    private boolean isCollectedByUser(Long loginUserId, Long postId) {
        if (loginUserId == null || postId == null) {
            return false;
        }
        LambdaQueryWrapper<com.graProject.graBackend.entity.PostCollectionDO> wrapper = new LambdaQueryWrapper<com.graProject.graBackend.entity.PostCollectionDO>()
                .eq(com.graProject.graBackend.entity.PostCollectionDO::getUserId, loginUserId)
                .eq(com.graProject.graBackend.entity.PostCollectionDO::getPostId, postId);
        return postCollectionMapper.selectCount(wrapper) > 0;
    }

    /**
     * 将贴文分页结果转换为 DTO 分页结果。
     *
     * @param doPage      原始贴文分页结果
     * @param useSummary  是否将正文转换为摘要
     * @param loginUserId 当前登录用户 ID（可为空）
     * @return DTO 分页结果
     */
    private IPage<ForumPostDTO> buildForumPostPage(IPage<ForumPostDO> doPage, boolean useSummary, Long loginUserId) {
        Map<String, String> sectionNameMap = resolveSectionNameMap(doPage.getRecords());
        Map<Long, UserDO> authorMap = resolveAuthorMap(doPage.getRecords());
        Set<Long> collectedPostIds = resolveCollectedPostIds(loginUserId, doPage.getRecords());
        List<ForumPostDTO> dtoRecords = new ArrayList<>();
        if (doPage.getRecords() != null) {
            for (ForumPostDO forumPostDO : doPage.getRecords()) {
                if (forumPostDO == null) {
                    continue;
                }
                ForumPostDTO dto = new ForumPostDTO();
                BeanUtils.copyProperties(forumPostDO, dto);
                if (useSummary) {
                    dto.setContent(RichTextUtil.toSummary(forumPostDO.getContent(), 200));
                }
                if (StringUtils.hasText(dto.getSectionCode())) {
                    dto.setSectionName(sectionNameMap.get(dto.getSectionCode()));
                }
                if (forumPostDO.getIsAnonymous() == null || forumPostDO.getIsAnonymous() == 0) {
                    UserDO author = authorMap.get(forumPostDO.getUserId());
                    if (author != null) {
                        dto.setAuthorNickname(author.getNickname());
                        dto.setAuthorAvatar(author.getAvatar());
                    }
                }
                dto.setCollected(collectedPostIds.contains(forumPostDO.getId()));
                dtoRecords.add(dto);
            }
        }
        Page<ForumPostDTO> dtoPage = new Page<>(doPage.getCurrent(), doPage.getSize(), doPage.getTotal());
        dtoPage.setRecords(dtoRecords);
        return dtoPage;
    }

    /**
     * 校验登录用户。
     *
     * @param loginUser 当前登录用户
     * @return 登录用户 ID
     */
    private Long validateLoginUser(UserDTO loginUser) {
        if (loginUser == null || loginUser.getId() == null) {
            throw new UserLoginException(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        return loginUser.getId();
    }

    /**
     * 校验贴文是否存在且已发布。
     *
     * @param postId 贴文 ID
     * @return 贴文记录
     */
    private ForumPostDO requirePublishedPost(Long postId) {
        if (postId == null) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "贴文ID不能为空");
        }
        ForumPostDO forumPostDO = selectPostById(postId);
        if (forumPostDO == null) {
            throw new UserLoginException(HttpCode.NOT_FOUND, "贴文不存在");
        }
        if (forumPostDO.getStatus() == null || forumPostDO.getStatus() != 1) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "当前贴文不可收藏");
        }
        return forumPostDO;
    }

    /**
     * 记录用户浏览贴文历史，并累加浏览量。
     *
     * @param userId 浏览用户 ID
     * @param postId 贴文 ID
     */
    private void recordBrowseHistory(Long userId, Long postId) {
        LambdaQueryWrapper<com.graProject.graBackend.entity.UserBrowseDO> wrapper = new LambdaQueryWrapper<com.graProject.graBackend.entity.UserBrowseDO>()
                .eq(com.graProject.graBackend.entity.UserBrowseDO::getUserId, userId)
                .eq(com.graProject.graBackend.entity.UserBrowseDO::getBrowseType, 1)
                .eq(com.graProject.graBackend.entity.UserBrowseDO::getRelateId, postId)
                .last("limit 1");
        com.graProject.graBackend.entity.UserBrowseDO userBrowseDO = userBrowseMapper.selectOne(wrapper);
        LocalDateTime now = LocalDateTime.now();
        if (userBrowseDO == null) {
            userBrowseDO = new com.graProject.graBackend.entity.UserBrowseDO();
            userBrowseDO.setUserId(userId);
            userBrowseDO.setBrowseType(1);
            userBrowseDO.setRelateId(postId);
            userBrowseDO.setBrowseTime(now);
            userBrowseMapper.insert(userBrowseDO);
        } else {
            userBrowseDO.setBrowseTime(now);
            userBrowseMapper.updateById(userBrowseDO);
        }
        forumPostMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<ForumPostDO>()
                        .eq(ForumPostDO::getId, postId)
                        .setSql("view_count = view_count + 1"));
    }

    /**
     * 按输入顺序批量查询贴文。
     *
     * @param postIds 贴文 ID 列表
     * @return 有序贴文列表
     */
    private List<ForumPostDO> listPostsByIdsInOrder(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        List<Long> filteredIds = postIds.stream().filter(java.util.Objects::nonNull).toList();
        if (filteredIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        LambdaQueryWrapper<ForumPostDO> wrapper = new LambdaQueryWrapper<ForumPostDO>()
                .in(ForumPostDO::getId, new java.util.HashSet<>(filteredIds));
        List<ForumPostDO> posts = forumPostMapper.selectList(wrapper);
        if (posts == null || posts.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        Map<Long, ForumPostDO> postMap = posts.stream()
                .filter(post -> post != null && post.getId() != null)
                .collect(Collectors.toMap(ForumPostDO::getId, Function.identity(), (a, b) -> a));
        List<ForumPostDO> orderedPosts = new ArrayList<>();
        Set<Long> addedIds = new java.util.HashSet<>();
        for (Long postId : filteredIds) {
            if (addedIds.contains(postId)) {
                continue;
            }
            ForumPostDO post = postMap.get(postId);
            if (post != null) {
                orderedPosts.add(post);
                addedIds.add(postId);
            }
        }
        return orderedPosts;
    }

    /**
     * 填充贴文作者昵称与头像。
     *
     * @param dto    贴文 DTO
     * @param userId 作者用户 ID
     */
    private void fillAuthor(ForumPostDTO dto, Long userId) {
        if (dto == null || userId == null) {
            return;
        }
        UserDO user = userMapper.selectById(userId);
        if (user == null) {
            return;
        }
        dto.setAuthorNickname(user.getNickname());
        dto.setAuthorAvatar(user.getAvatar());
    }

    /**
     * 根据 ID 查询贴文记录。
     *
     * @param postId 贴文 ID
     * @return 贴文 DO；不存在时返回 null
     */
    private ForumPostDO selectPostById(Long postId) {
        if (postId == null) {
            return null;
        }
        return forumPostMapper.selectById(postId);
    }
}
