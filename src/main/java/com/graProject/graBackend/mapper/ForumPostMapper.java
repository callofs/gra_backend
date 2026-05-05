package com.graProject.graBackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graProject.graBackend.entity.ForumPostDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 论坛贴文表
 */
@Mapper
public interface ForumPostMapper extends BaseMapper<ForumPostDO> {
}
