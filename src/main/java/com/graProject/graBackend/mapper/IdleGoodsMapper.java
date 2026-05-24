package com.graProject.graBackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graProject.graBackend.entity.IdleGoodsDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 闲置物品表。
 */
@Mapper
public interface IdleGoodsMapper extends BaseMapper<IdleGoodsDO> {
}
