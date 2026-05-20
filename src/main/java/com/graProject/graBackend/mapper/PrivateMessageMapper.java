package com.graProject.graBackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graProject.graBackend.entity.PrivateMessageDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户私信 Mapper。
 */
@Mapper
public interface PrivateMessageMapper extends BaseMapper<PrivateMessageDO> {
}
