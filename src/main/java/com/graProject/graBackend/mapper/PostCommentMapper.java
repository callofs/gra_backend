package com.graProject.graBackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graProject.graBackend.entity.PostCommentDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 贴文评论表 Mapper。
 */
@Mapper
public interface PostCommentMapper extends BaseMapper<PostCommentDO> {
}
