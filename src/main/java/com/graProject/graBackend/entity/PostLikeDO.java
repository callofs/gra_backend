package com.graProject.graBackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 贴文点赞表
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_post_like")
public class PostLikeDO implements Serializable {
    /**
     * 点赞ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 点赞类型 1=贴文点赞 2=评论点赞
     */

    @TableField("like_type")
    private Integer likeType;
    /**
     * 关联ID（贴文ID/评论ID）
     */

    @TableField("relate_id")
    private Long relateId;
    /**
     * 点赞用户ID，逻辑关联t_user.id
     */

    @TableField("user_id")
    private Long userId;
    /**
     * 点赞时间
     */

    @TableField("create_time")
    private LocalDateTime createTime;
}
