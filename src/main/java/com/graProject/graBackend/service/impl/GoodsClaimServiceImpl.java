package com.graProject.graBackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graProject.graBackend.common.exception.User.UserLoginException;
import com.graProject.graBackend.common.result.HttpCode;
import com.graProject.graBackend.dto.GoodsClaimDTO;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.entity.GoodsClaimDO;
import com.graProject.graBackend.entity.IdleGoodsDO;
import com.graProject.graBackend.entity.UserDO;
import com.graProject.graBackend.mapper.GoodsClaimMapper;
import com.graProject.graBackend.mapper.IdleGoodsMapper;
import com.graProject.graBackend.mapper.UserMapper;
import com.graProject.graBackend.service.GoodsClaimService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GoodsClaimServiceImpl implements GoodsClaimService {

    private final GoodsClaimMapper goodsClaimMapper;
    private final IdleGoodsMapper idleGoodsMapper;
    private final UserMapper userMapper;

    public GoodsClaimServiceImpl(GoodsClaimMapper goodsClaimMapper, IdleGoodsMapper idleGoodsMapper,
            UserMapper userMapper) {
        this.goodsClaimMapper = goodsClaimMapper;
        this.idleGoodsMapper = idleGoodsMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GoodsClaimDTO createClaim(UserDTO loginUser, GoodsClaimDTO claimDTO) {
        Long claimerId = requireLoginUserId(loginUser);
        validateClaimRequest(claimDTO);
        IdleGoodsDO goods = loadAvailableGoods(claimDTO.getGoodsId());
        if (claimerId.equals(goods.getUserId())) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "不能认领自己发布的物品");
        }
        if (goods.getStatus() != null && goods.getStatus() != 0) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "该物品当前不可认领");
        }

        LambdaQueryWrapper<GoodsClaimDO> duplicateWrapper = new LambdaQueryWrapper<GoodsClaimDO>()
                .eq(GoodsClaimDO::getGoodsId, claimDTO.getGoodsId())
                .eq(GoodsClaimDO::getClaimerId, claimerId)
                .in(GoodsClaimDO::getStatus, List.of(0, 1));
        if (goodsClaimMapper.selectCount(duplicateWrapper) > 0) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "请勿重复提交认领");
        }

        GoodsClaimDO claimDO = new GoodsClaimDO();
        BeanUtils.copyProperties(claimDTO, claimDO);
        LocalDateTime now = LocalDateTime.now();
        claimDO.setClaimerId(claimerId);
        claimDO.setPublisherId(goods.getUserId());
        claimDO.setStatus(0);
        claimDO.setCreateTime(now);
        claimDO.setUpdateTime(now);
        claimDO.setIsDelete(0);
        goodsClaimMapper.insert(claimDO);

        GoodsClaimDTO result = new GoodsClaimDTO();
        BeanUtils.copyProperties(claimDO, result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelClaim(UserDTO loginUser, Long claimId) {
        Long claimerId = requireLoginUserId(loginUser);
        GoodsClaimDO claimDO = loadClaim(claimId);
        if (!claimerId.equals(claimDO.getClaimerId())) {
            throw new UserLoginException(HttpCode.FORBIDDEN, "无权取消该认领");
        }
        if (claimDO.getStatus() != null && claimDO.getStatus() != 0 && claimDO.getStatus() != 1) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "该认领已处理，无法取消");
        }
        claimDO.setStatus(4);
        claimDO.setUpdateTime(LocalDateTime.now());
        int updated = goodsClaimMapper.updateById(claimDO);
        refreshGoodsStatusAfterClaimChange(claimDO.getGoodsId());
        return updated > 0;
    }

    @Override
    public IPage<GoodsClaimDTO> listMyClaims(UserDTO loginUser, long page, long size) {
        Long claimerId = requireLoginUserId(loginUser);
        Page<GoodsClaimDO> pageParam = new Page<>(Math.max(1, page), Math.min(Math.max(1, size), 50));
        LambdaQueryWrapper<GoodsClaimDO> wrapper = new LambdaQueryWrapper<GoodsClaimDO>()
                .eq(GoodsClaimDO::getClaimerId, claimerId)
                .eq(GoodsClaimDO::getIsDelete, 0)
                .orderByDesc(GoodsClaimDO::getCreateTime)
                .orderByDesc(GoodsClaimDO::getId);
        return goodsClaimMapper.selectPage(pageParam, wrapper).convert(this::buildClaimDTO);
    }

    @Override
    public IPage<GoodsClaimDTO> listReceivedClaims(UserDTO loginUser, Long goodsId, long page, long size) {
        Long publisherId = requireLoginUserId(loginUser);
        Page<GoodsClaimDO> pageParam = new Page<>(Math.max(1, page), Math.min(Math.max(1, size), 50));
        LambdaQueryWrapper<GoodsClaimDO> wrapper = new LambdaQueryWrapper<GoodsClaimDO>()
                .eq(GoodsClaimDO::getPublisherId, publisherId)
                .eq(GoodsClaimDO::getIsDelete, 0)
                .eq(goodsId != null, GoodsClaimDO::getGoodsId, goodsId)
                .orderByDesc(GoodsClaimDO::getCreateTime)
                .orderByDesc(GoodsClaimDO::getId);
        IPage<GoodsClaimDO> doPage = goodsClaimMapper.selectPage(pageParam, wrapper);
        Map<Long, String> claimerNameMap = resolveClaimerNicknameMap(doPage.getRecords());
        return doPage.convert(claimDO -> buildClaimDTO(claimDO, claimerNameMap));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateClaimStatus(UserDTO loginUser, Long claimId, Integer status) {
        Long publisherId = requireLoginUserId(loginUser);
        if (status == null || (status != 1 && status != 2 && status != 3)) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "状态值不合法");
        }
        GoodsClaimDO claimDO = loadClaim(claimId);
        if (!publisherId.equals(claimDO.getPublisherId())) {
            throw new UserLoginException(HttpCode.FORBIDDEN, "无权处理该认领");
        }
        if (claimDO.getStatus() != null && claimDO.getStatus() == 4) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "该认领已取消");
        }
        if (status == 3 && (claimDO.getStatus() == null || claimDO.getStatus() != 1)) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "仅已同意的认领可以完成");
        }
        if (status == 1 && claimDO.getStatus() != null && claimDO.getStatus() != 0) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "仅待确认的认领可以同意");
        }

        claimDO.setStatus(status);
        claimDO.setUpdateTime(LocalDateTime.now());
        int updated = goodsClaimMapper.updateById(claimDO);
        if (updated > 0) {
            if (status == 1) {
                updateGoodsStatus(claimDO.getGoodsId(), 1);
                rejectOtherPendingClaims(claimDO.getGoodsId(), claimDO.getId());
            } else if (status == 3) {
                updateGoodsStatus(claimDO.getGoodsId(), 2);
                closeOtherClaims(claimDO.getGoodsId(), claimDO.getId());
            } else if (status == 2) {
                refreshGoodsStatusAfterClaimChange(claimDO.getGoodsId());
            }
        }
        return updated > 0;
    }

    private void rejectOtherPendingClaims(Long goodsId, Long excludeClaimId) {
        if (goodsId == null) {
            return;
        }
        LambdaUpdateWrapper<GoodsClaimDO> wrapper = new LambdaUpdateWrapper<GoodsClaimDO>()
                .eq(GoodsClaimDO::getGoodsId, goodsId)
                .ne(GoodsClaimDO::getId, excludeClaimId)
                .in(GoodsClaimDO::getStatus, List.of(0, 1));
        goodsClaimMapper.update(null, wrapper
                .set(GoodsClaimDO::getStatus, 2)
                .set(GoodsClaimDO::getUpdateTime, LocalDateTime.now()));
    }

    private void closeOtherClaims(Long goodsId, Long excludeClaimId) {
        if (goodsId == null) {
            return;
        }
        LambdaUpdateWrapper<GoodsClaimDO> wrapper = new LambdaUpdateWrapper<GoodsClaimDO>()
                .eq(GoodsClaimDO::getGoodsId, goodsId)
                .ne(GoodsClaimDO::getId, excludeClaimId)
                .in(GoodsClaimDO::getStatus, List.of(0, 1, 2));
        goodsClaimMapper.update(null, wrapper
                .set(GoodsClaimDO::getStatus, 4)
                .set(GoodsClaimDO::getUpdateTime, LocalDateTime.now()));
    }

    private void refreshGoodsStatusAfterClaimChange(Long goodsId) {
        if (goodsId == null) {
            return;
        }
        IdleGoodsDO goods = idleGoodsMapper.selectById(goodsId);
        if (goods == null || goods.getIsDelete() != null && goods.getIsDelete() == 1) {
            return;
        }
        if (goods.getStatus() != null && goods.getStatus() == 2) {
            return;
        }
        LambdaQueryWrapper<GoodsClaimDO> approvedWrapper = new LambdaQueryWrapper<GoodsClaimDO>()
                .eq(GoodsClaimDO::getGoodsId, goodsId)
                .eq(GoodsClaimDO::getStatus, 1);
        long approvedCount = goodsClaimMapper.selectCount(approvedWrapper);
        if (approvedCount == 0 && (goods.getStatus() == null || goods.getStatus() == 1)) {
            updateGoodsStatus(goodsId, 0);
        } else if (approvedCount > 0) {
            updateGoodsStatus(goodsId, 1);
        }
    }

    private void updateGoodsStatus(Long goodsId, Integer status) {
        if (goodsId == null || status == null) {
            return;
        }
        IdleGoodsDO update = new IdleGoodsDO();
        update.setId(goodsId);
        update.setStatus(status);
        update.setUpdateTime(LocalDateTime.now());
        idleGoodsMapper.updateById(update);
    }

    private GoodsClaimDO loadClaim(Long claimId) {
        if (claimId == null) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "认领ID不能为空");
        }
        GoodsClaimDO claimDO = goodsClaimMapper.selectById(claimId);
        if (claimDO == null || (claimDO.getIsDelete() != null && claimDO.getIsDelete() == 1)) {
            throw new UserLoginException(HttpCode.NOT_FOUND, "认领记录不存在");
        }
        return claimDO;
    }

    private IdleGoodsDO loadAvailableGoods(Long goodsId) {
        if (goodsId == null) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "物品ID不能为空");
        }
        IdleGoodsDO goods = idleGoodsMapper.selectById(goodsId);
        if (goods == null || goods.getIsDelete() != null && goods.getIsDelete() == 1) {
            throw new UserLoginException(HttpCode.NOT_FOUND, "闲置物品不存在");
        }
        return goods;
    }

    private void validateClaimRequest(GoodsClaimDTO claimDTO) {
        if (claimDTO == null || claimDTO.getGoodsId() == null) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "物品ID不能为空");
        }
        if (!StringUtils.hasText(claimDTO.getClaimDesc())) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "认领说明不能为空");
        }
        if (!StringUtils.hasText(claimDTO.getContactInfo())) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "联系方式不能为空");
        }
    }

    private GoodsClaimDTO buildClaimDTO(GoodsClaimDO claimDO) {
        GoodsClaimDTO dto = new GoodsClaimDTO();
        if (claimDO != null) {
            BeanUtils.copyProperties(claimDO, dto);
        }
        return dto;
    }

    private GoodsClaimDTO buildClaimDTO(GoodsClaimDO claimDO, Map<Long, String> claimerNameMap) {
        GoodsClaimDTO dto = buildClaimDTO(claimDO);
        if (claimDO != null && claimerNameMap != null) {
            dto.setApplicantName(claimerNameMap.get(claimDO.getClaimerId()));
        }
        return dto;
    }

    private Map<Long, String> resolveClaimerNicknameMap(List<GoodsClaimDO> records) {
        if (records == null || records.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        Set<Long> claimerIds = records.stream()
                .filter(record -> record != null && record.getClaimerId() != null)
                .map(GoodsClaimDO::getClaimerId)
                .collect(Collectors.toSet());
        if (claimerIds.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<UserDO>()
                .in(UserDO::getId, claimerIds)
                .eq(UserDO::getIsDelete, 0);
        List<UserDO> users = userMapper.selectList(wrapper);
        if (users == null || users.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        return users.stream()
                .filter(user -> user != null && user.getId() != null)
                .collect(Collectors.toMap(UserDO::getId, UserDO::getNickname, (left, right) -> left));
    }

    private Long requireLoginUserId(UserDTO loginUser) {
        if (loginUser == null || loginUser.getId() == null) {
            throw new UserLoginException(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        return loginUser.getId();
    }
}
