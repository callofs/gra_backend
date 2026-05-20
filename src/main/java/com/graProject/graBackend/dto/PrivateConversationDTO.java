package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 私聊会话摘要。
 */
@Data
public class PrivateConversationDTO implements Serializable {

    /**
     * 对方用户 ID。
     */
    private Long targetUserId;

    /**
     * 对方用户昵称。
     */
    private String targetNickname;

    /**
     * 最近一条消息的发送者 ID。
     */
    private Long lastSenderId;

    /**
     * 最近一条消息类型。
     */
    private Integer lastMessageType;

    /**
     * 最近一条消息内容。
     */
    private String lastMessageContent;

    /**
     * 最近一条消息时间。
     */
    private LocalDateTime lastMessageTime;

    /**
     * 当前会话未读消息数。
     */
    private Integer unreadCount;
}
