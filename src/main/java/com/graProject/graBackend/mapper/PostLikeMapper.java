package com.graProject.graBackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graProject.graBackend.entity.PostLikeDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 点赞表 Mapper。
 */
@Mapper
public interface PostLikeMapper extends BaseMapper<PostLikeDO> {
}
