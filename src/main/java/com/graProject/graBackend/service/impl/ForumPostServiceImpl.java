package com.graProject.graBackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graProject.graBackend.common.utils.RichTextUtil;
import com.graProject.graBackend.dto.ForumPostDTO;
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
     * 构造方法。
     *
     * @param forumPostMapper 贴文 Mapper
     * @param dictMapper      字典 Mapper
     */
    public ForumPostServiceImpl(ForumPostMapper forumPostMapper, DictMapper dictMapper, UserMapper userMapper) {
        this.forumPostMapper = forumPostMapper;
        this.dictMapper = dictMapper;
        this.userMapper = userMapper;
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
     * @param postId 贴文 ID
     * @return 贴文详情 DTO；不存在时返回 null
     */
    @Override
    public ForumPostDTO getForumPostDetail(Long postId) {
        ForumPostDO forumPostDO = selectPostById(postId);
        if (forumPostDO == null) {
            return null;
        }
        ForumPostDTO dto = new ForumPostDTO();
        BeanUtils.copyProperties(forumPostDO, dto);

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
     * @return 分页摘要结果
     */
    @Override
    public IPage<ForumPostDTO> listForumPostSummaries(long page, long size, String sectionCode, String keyword) {
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

        Map<String, String> sectionNameMap = resolveSectionNameMap(doPage.getRecords());
        Map<Long, UserDO> authorMap = resolveAuthorMap(doPage.getRecords());
        List<ForumPostDTO> dtoRecords = new ArrayList<>();
        if (doPage.getRecords() != null) {
            for (ForumPostDO forumPostDO : doPage.getRecords()) {
                if (forumPostDO == null) {
                    continue;
                }
                ForumPostDTO dto = new ForumPostDTO();
                BeanUtils.copyProperties(forumPostDO, dto);
                dto.setContent(RichTextUtil.toSummary(forumPostDO.getContent(), 200));
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
                dtoRecords.add(dto);
            }
        }

        Page<ForumPostDTO> dtoPage = new Page<>(doPage.getCurrent(), doPage.getSize(), doPage.getTotal());
        dtoPage.setRecords(dtoRecords);
        return dtoPage;
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
