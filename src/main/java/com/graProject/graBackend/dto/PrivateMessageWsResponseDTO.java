package com.graProject.graBackend.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * WebSocket 私聊消息响应对象。
 */
@Data
@Builder
public class PrivateMessageWsResponseDTO implements Serializable {

    /**
     * 响应事件类型。
     */
    private String event;

    /**
     * 响应提示信息。
     */
    private String message;

    /**
     * 私聊消息数据。
     */
    private PrivateMessageDTO data;

    /**
     * 当前用户未读消息总数。
     */
    private Long unreadCount;
}
