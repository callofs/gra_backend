package com.graProject.graBackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graProject.graBackend.entity.UserBrowseDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户浏览记录表 Mapper。
 */
@Mapper
public interface UserBrowseMapper extends BaseMapper<UserBrowseDO> {
}
