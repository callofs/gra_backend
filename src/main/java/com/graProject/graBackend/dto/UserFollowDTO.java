package com.graProject.graBackend.dto;

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
public class UserFollowDTO implements Serializable {
    /**
     * 关注记录ID
     */

    private Long id;
    /**
     * 关注者ID，逻辑关联t_user.id
     */

    private Long followerId;
    /**
     * 被关注者ID，逻辑关联t_user.id
     */

    private Long followedId;
    /**
     * 关注时间
     */

    private LocalDateTime createTime;
}
