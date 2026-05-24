package com.graProject.graBackend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graProject.graBackend.dto.IdleGoodsDTO;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.entity.IdleGoodsDO;
import org.springframework.stereotype.Service;

/**
 * 闲置物品服务。
 */
@Service
public interface IdleGoodsService {

    /**
     * 发布闲置物品。
     *
     * @param idleGoodsDO 闲置物品实体
     * @return 发布结果
     */
    String createIdleGoods(IdleGoodsDO idleGoodsDO);

    /**
     * 分页查询闲置物品列表。
     *
     * @param page          页码
     * @param size          每页条数
     * @param goodsTypeCode 物品类型编码
     * @param keyword       标题或名称关键字
     * @return 分页结果
     */
    IPage<IdleGoodsDTO> listIdleGoods(long page, long size, String goodsTypeCode, String keyword);

    /**
     * 获取闲置物品详情。
     *
     * @param id 闲置物品 ID
     * @return 闲置物品详情
     */
    IdleGoodsDTO getIdleGoodsDetail(Long id);

    /**
     * 修改闲置物品。
     *
     * @param loginUser    当前登录用户
     * @param idleGoodsDO  待修改的闲置物品实体
     * @return 修改结果
     */
    String updateIdleGoods(UserDTO loginUser, IdleGoodsDO idleGoodsDO);

    /**
     * 分页查询当前登录用户发布的闲置物品。
     *
     * @param loginUser 当前登录用户
     * @param page      页码
     * @param size      每页条数
     * @return 分页结果
     */
    IPage<IdleGoodsDTO> listMyIdleGoods(UserDTO loginUser, long page, long size);
}
