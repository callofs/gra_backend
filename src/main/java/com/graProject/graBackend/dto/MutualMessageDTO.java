package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 互助站内信表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MutualMessageDTO implements Serializable {
    /**
     * 消息ID
     */

    private Long id;
    /**
     * 关联物品ID，逻辑关联t_idle_goods.id
     */

    private Long goodsId;
    /**
     * 发送者ID，逻辑关联t_user.id
     */

    private Long senderId;
    /**
     * 接收者ID，逻辑关联t_user.id
     */

    private Long receiverId;
    /**
     * 消息内容
     */

    private String content;
    /**
     * 是否已读 0=未读 1=已读
     */

    private Integer isRead;
    /**
     * 发送时间
     */

    private LocalDateTime createTime;
    /**
     * 逻辑删除 0=未删除 1=已删除
     */

    private Integer isDelete;
}
