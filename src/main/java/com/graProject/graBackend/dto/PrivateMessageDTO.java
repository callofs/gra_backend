package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 私信表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PrivateMessageDTO implements Serializable {
    /**
     * 私信ID
     */

    private Long id;
    /**
     * 发送者ID，逻辑关联t_user.id
     */

    private Long senderId;
    /**
     * 接收者ID，逻辑关联t_user.id
     */

    private Long receiverId;
    /**
     * 消息类型 1=文本 2=图片 3=表情
     */

    private Integer msgType;
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
