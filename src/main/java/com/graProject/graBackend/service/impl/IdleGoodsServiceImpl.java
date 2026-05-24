package com.graProject.graBackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graProject.graBackend.common.exception.User.UserLoginException;
import com.graProject.graBackend.common.result.HttpCode;
import com.graProject.graBackend.dto.IdleGoodsDTO;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.entity.DictDO;
import com.graProject.graBackend.entity.IdleGoodsDO;
import com.graProject.graBackend.entity.UserDO;
import com.graProject.graBackend.mapper.DictMapper;
import com.graProject.graBackend.mapper.IdleGoodsMapper;
import com.graProject.graBackend.mapper.UserMapper;
import com.graProject.graBackend.service.IdleGoodsService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 闲置物品服务实现。
 */
@Service
public class IdleGoodsServiceImpl implements IdleGoodsService {

    private final IdleGoodsMapper idleGoodsMapper;
    private final DictMapper dictMapper;
    private final UserMapper userMapper;

    public IdleGoodsServiceImpl(IdleGoodsMapper idleGoodsMapper, DictMapper dictMapper, UserMapper userMapper) {
        this.idleGoodsMapper = idleGoodsMapper;
        this.dictMapper = dictMapper;
        this.userMapper = userMapper;
    }

    /**
     * 发布闲置物品。
     *
     * @param idleGoodsDO 闲置物品实体
     * @return 发布结果
     */
    @Override
    public String createIdleGoods(IdleGoodsDO idleGoodsDO) {
        if (idleGoodsDO == null) {
            throw new IllegalArgumentException("闲置物品参数不能为空");
        }
        validateIdleGoodsForCreate(idleGoodsDO);
        LocalDateTime now = LocalDateTime.now();
        if (idleGoodsDO.getViewCount() == null) {
            idleGoodsDO.setViewCount(0);
        }
        if (idleGoodsDO.getStatus() == null) {
            idleGoodsDO.setStatus(0);
        }
        if (idleGoodsDO.getCreateTime() == null) {
            idleGoodsDO.setCreateTime(now);
        }
        idleGoodsDO.setUpdateTime(now);
        if (idleGoodsDO.getIsDelete() == null) {
            idleGoodsDO.setIsDelete(0);
        }
        idleGoodsMapper.insert(idleGoodsDO);
        return "发布成功";
    }

    /**
     * 分页查询闲置物品列表。
     *
     * @param page          页码
     * @param size          每页条数
     * @param goodsTypeCode 物品类型编码
     * @param keyword       标题或名称关键字
     * @return 分页结果
     */
    @Override
    public IPage<IdleGoodsDTO> listIdleGoods(long page, long size, String goodsTypeCode, String keyword) {
        long current = Math.max(1, page);
        long pageSize = Math.min(Math.max(1, size), 50);
        LambdaQueryWrapper<IdleGoodsDO> wrapper = new LambdaQueryWrapper<IdleGoodsDO>()
                .ne(IdleGoodsDO::getStatus, 3)
                .orderByDesc(IdleGoodsDO::getCreateTime)
                .orderByDesc(IdleGoodsDO::getId);
        if (StringUtils.hasText(goodsTypeCode)) {
            wrapper.eq(IdleGoodsDO::getGoodsTypeCode, goodsTypeCode.trim());
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(IdleGoodsDO::getTitle, keyword.trim())
                    .or()
                    .like(IdleGoodsDO::getName, keyword.trim()));
        }
        IPage<IdleGoodsDO> doPage = idleGoodsMapper.selectPage(new Page<>(current, pageSize), wrapper);
        return buildIdleGoodsPage(doPage);
    }

    /**
     * 获取闲置物品详情。
     *
     * @param id 闲置物品 ID
     * @return 闲置物品详情
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public IdleGoodsDTO getIdleGoodsDetail(Long id) {
        IdleGoodsDO idleGoodsDO = selectIdleGoodsById(id);
        if (idleGoodsDO == null) {
            return null;
        }
        idleGoodsMapper.update(null, new LambdaUpdateWrapper<IdleGoodsDO>()
                .eq(IdleGoodsDO::getId, id)
                .setSql("view_count = view_count + 1"));
        idleGoodsDO.setViewCount(idleGoodsDO.getViewCount() == null ? 1 : idleGoodsDO.getViewCount() + 1);
        return buildIdleGoodsDTO(idleGoodsDO);
    }

    /**
     * 修改闲置物品。
     *
     * @param loginUser   当前登录用户
     * @param idleGoodsDO 待修改的闲置物品实体
     * @return 修改结果
     */
    @Override
    public String updateIdleGoods(UserDTO loginUser, IdleGoodsDO idleGoodsDO) {
        Long userId = validateLoginUser(loginUser);
        if (idleGoodsDO == null || idleGoodsDO.getId() == null) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "闲置物品ID不能为空");
        }
        IdleGoodsDO record = selectIdleGoodsById(idleGoodsDO.getId());
        if (record == null) {
            throw new UserLoginException(HttpCode.NOT_FOUND, "闲置物品不存在");
        }
        if (!userId.equals(record.getUserId())) {
            throw new UserLoginException(HttpCode.FORBIDDEN, "无权修改该闲置物品");
        }
        validateIdleGoodsForUpdate(idleGoodsDO);
        record.setGoodsTypeCode(idleGoodsDO.getGoodsTypeCode());
        record.setTitle(idleGoodsDO.getTitle());
        record.setName(idleGoodsDO.getName());
        record.setCoverImages(idleGoodsDO.getCoverImages());
        record.setDescription(idleGoodsDO.getDescription());
        record.setFitAge(idleGoodsDO.getFitAge());
        record.setSize(idleGoodsDO.getSize());
        record.setSeason(idleGoodsDO.getSeason());
        record.setMaterial(idleGoodsDO.getMaterial());
        record.setOldDegree(idleGoodsDO.getOldDegree());
        record.setPickUpType(idleGoodsDO.getPickUpType());
        record.setAddress(idleGoodsDO.getAddress());
        if (idleGoodsDO.getStatus() != null) {
            record.setStatus(idleGoodsDO.getStatus());
        }
        record.setUpdateTime(LocalDateTime.now());
        idleGoodsMapper.updateById(record);
        return "修改成功";
    }

    /**
     * 分页查询当前登录用户发布的闲置物品。
     *
     * @param loginUser 当前登录用户
     * @param page      页码
     * @param size      每页条数
     * @return 分页结果
     */
    @Override
    public IPage<IdleGoodsDTO> listMyIdleGoods(UserDTO loginUser, long page, long size) {
        Long userId = validateLoginUser(loginUser);
        long current = Math.max(1, page);
        long pageSize = Math.min(Math.max(1, size), 50);
        LambdaQueryWrapper<IdleGoodsDO> wrapper = new LambdaQueryWrapper<IdleGoodsDO>()
                .eq(IdleGoodsDO::getUserId, userId)
                .orderByDesc(IdleGoodsDO::getCreateTime)
                .orderByDesc(IdleGoodsDO::getId);
        IPage<IdleGoodsDO> doPage = idleGoodsMapper.selectPage(new Page<>(current, pageSize), wrapper);
        return buildIdleGoodsPage(doPage);
    }

    private IPage<IdleGoodsDTO> buildIdleGoodsPage(IPage<IdleGoodsDO> doPage) {
        List<IdleGoodsDO> records = doPage.getRecords() == null ? Collections.emptyList() : doPage.getRecords();
        Map<String, String> goodsTypeNameMap = resolveGoodsTypeNameMap(records);
        Map<Long, UserDO> publisherMap = resolvePublisherMap(records);
        List<IdleGoodsDTO> dtoRecords = new ArrayList<>();
        for (IdleGoodsDO record : records) {
            if (record == null) {
                continue;
            }
            dtoRecords.add(buildIdleGoodsDTO(record, goodsTypeNameMap, publisherMap));
        }
        Page<IdleGoodsDTO> dtoPage = new Page<>(doPage.getCurrent(), doPage.getSize(), doPage.getTotal());
        dtoPage.setRecords(dtoRecords);
        return dtoPage;
    }

    private IdleGoodsDTO buildIdleGoodsDTO(IdleGoodsDO idleGoodsDO) {
        if (idleGoodsDO == null) {
            return null;
        }
        List<IdleGoodsDO> records = Collections.singletonList(idleGoodsDO);
        return buildIdleGoodsDTO(idleGoodsDO, resolveGoodsTypeNameMap(records), resolvePublisherMap(records));
    }

    private IdleGoodsDTO buildIdleGoodsDTO(IdleGoodsDO idleGoodsDO, Map<String, String> goodsTypeNameMap,
            Map<Long, UserDO> publisherMap) {
        IdleGoodsDTO dto = new IdleGoodsDTO();
        dto.setId(idleGoodsDO.getId());
        dto.setUserId(idleGoodsDO.getUserId());
        dto.setGoodsTypeCode(idleGoodsDO.getGoodsTypeCode());
        dto.setTitle(idleGoodsDO.getTitle());
        dto.setName(idleGoodsDO.getName());
        dto.setCoverImages(idleGoodsDO.getCoverImages());
        dto.setDescription(idleGoodsDO.getDescription());
        dto.setFitAge(idleGoodsDO.getFitAge());
        dto.setSize(idleGoodsDO.getSize());
        dto.setSeason(idleGoodsDO.getSeason());
        dto.setMaterial(idleGoodsDO.getMaterial());
        dto.setOldDegree(idleGoodsDO.getOldDegree());
        dto.setPickUpType(idleGoodsDO.getPickUpType());
        dto.setAddress(idleGoodsDO.getAddress());
        dto.setViewCount(idleGoodsDO.getViewCount());
        dto.setStatus(idleGoodsDO.getStatus());
        dto.setCreateTime(idleGoodsDO.getCreateTime());
        dto.setUpdateTime(idleGoodsDO.getUpdateTime());
        dto.setIsDelete(idleGoodsDO.getIsDelete());
        if (StringUtils.hasText(idleGoodsDO.getGoodsTypeCode())) {
            dto.setGoodsTypeName(goodsTypeNameMap.get(idleGoodsDO.getGoodsTypeCode()));
        }
        if (idleGoodsDO.getUserId() != null) {
            UserDO publisher = publisherMap.get(idleGoodsDO.getUserId());
            if (publisher != null) {
                dto.setPublisherNickname(publisher.getNickname());
                dto.setPublisherAvatar(publisher.getAvatar());
            }
        }
        return dto;
    }

    private Map<String, String> resolveGoodsTypeNameMap(List<IdleGoodsDO> records) {
        if (records == null || records.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        Set<String> codes = records.stream()
                .filter(record -> record != null && StringUtils.hasText(record.getGoodsTypeCode()))
                .map(IdleGoodsDO::getGoodsTypeCode)
                .collect(Collectors.toSet());
        if (codes.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        LambdaQueryWrapper<DictDO> wrapper = new LambdaQueryWrapper<DictDO>()
                .in(DictDO::getDictCode, codes);
        List<DictDO> dicts = dictMapper.selectList(wrapper);
        if (dicts == null || dicts.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        return dicts.stream()
                .filter(dict -> dict != null && StringUtils.hasText(dict.getDictCode()))
                .collect(Collectors.toMap(DictDO::getDictCode, DictDO::getDictName, (left, right) -> left));
    }

    private Map<Long, UserDO> resolvePublisherMap(List<IdleGoodsDO> records) {
        if (records == null || records.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        Set<Long> userIds = records.stream()
                .filter(record -> record != null && record.getUserId() != null)
                .map(IdleGoodsDO::getUserId)
                .collect(Collectors.toSet());
        if (userIds.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<UserDO>()
                .in(UserDO::getId, userIds)
                .eq(UserDO::getIsDelete, 0);
        List<UserDO> users = userMapper.selectList(wrapper);
        if (users == null || users.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        return users.stream()
                .filter(user -> user != null && user.getId() != null)
                .collect(Collectors.toMap(UserDO::getId, Function.identity(), (left, right) -> left));
    }

    private void validateIdleGoodsForCreate(IdleGoodsDO idleGoodsDO) {
        if (idleGoodsDO.getUserId() == null) {
            throw new IllegalArgumentException("发布者不能为空");
        }
        validateIdleGoodsCoreFields(idleGoodsDO);
    }

    private void validateIdleGoodsForUpdate(IdleGoodsDO idleGoodsDO) {
        validateIdleGoodsCoreFields(idleGoodsDO);
        if (idleGoodsDO.getStatus() != null && (idleGoodsDO.getStatus() < 0 || idleGoodsDO.getStatus() > 3)) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "物品状态不合法");
        }
    }

    private void validateIdleGoodsCoreFields(IdleGoodsDO idleGoodsDO) {
        if (!StringUtils.hasText(idleGoodsDO.getGoodsTypeCode())) {
            throw new IllegalArgumentException("物品类型不能为空");
        }
        if (!StringUtils.hasText(idleGoodsDO.getTitle())) {
            throw new IllegalArgumentException("物品标题不能为空");
        }
        if (!StringUtils.hasText(idleGoodsDO.getName())) {
            throw new IllegalArgumentException("物品名称不能为空");
        }
        if (!StringUtils.hasText(idleGoodsDO.getCoverImages())) {
            throw new IllegalArgumentException("物品图片不能为空");
        }
        if (!StringUtils.hasText(idleGoodsDO.getDescription())) {
            throw new IllegalArgumentException("物品描述不能为空");
        }
        if (idleGoodsDO.getPickUpType() == null || idleGoodsDO.getPickUpType() < 1 || idleGoodsDO.getPickUpType() > 3) {
            throw new IllegalArgumentException("取件方式不合法");
        }
        if ((idleGoodsDO.getPickUpType() == 1 || idleGoodsDO.getPickUpType() == 3)
                && !StringUtils.hasText(idleGoodsDO.getAddress())) {
            throw new IllegalArgumentException("自提地址不能为空");
        }
    }

    private Long validateLoginUser(UserDTO loginUser) {
        if (loginUser == null || loginUser.getId() == null) {
            throw new UserLoginException(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        return loginUser.getId();
    }

    private IdleGoodsDO selectIdleGoodsById(Long id) {
        if (id == null) {
            return null;
        }
        return idleGoodsMapper.selectById(id);
    }
}
