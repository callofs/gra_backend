package com.graProject.graBackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graProject.graBackend.entity.UserDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表 Mapper。
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {
}
