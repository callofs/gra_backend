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
 * 贴文收藏表
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_post_collection")
public class PostCollectionDO implements Serializable {
    /**
     * 收藏ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 收藏用户ID，逻辑关联t_user.id
     */

    @TableField("user_id")
    private Long userId;
    /**
     * 收藏贴文ID，逻辑关联t_forum_post.id
     */

    @TableField("post_id")
    private Long postId;
    /**
     * 自定义标签
     */

    @TableField("custom_tag")
    private String customTag;
    /**
     * 收藏备注
     */

    @TableField("remark")
    private String remark;
    /**
     * 收藏时间
     */

    @TableField("create_time")
    private LocalDateTime createTime;
}
