package com.graProject.graBackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graProject.graBackend.entity.PostCollectionDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 贴文收藏表 Mapper。
 */
@Mapper
public interface PostCollectionMapper extends BaseMapper<PostCollectionDO> {
}
