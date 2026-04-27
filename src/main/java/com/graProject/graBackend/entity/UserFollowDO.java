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
 * 用户关注表
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_user_follow")
public class UserFollowDO implements Serializable {
    /**
     * 关注记录ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 关注者ID，逻辑关联t_user.id
     */

    @TableField("follower_id")
    private Long followerId;
    /**
     * 被关注者ID，逻辑关联t_user.id
     */

    @TableField("followed_id")
    private Long followedId;
    /**
     * 关注时间
     */

    @TableField("create_time")
    private LocalDateTime createTime;
}
