package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * WebSocket 私聊消息请求参数。
 */
@Data
public class PrivateMessageWsRequestDTO implements Serializable {

    /**
     * 消息动作类型。
     */
    private String action;

    /**
     * 接收者用户 ID。
     */
    private Long receiverId;

    /**
     * 消息类型 1=文本 2=图片 3=表情。
     */
    private Integer msgType;

    /**
     * 消息内容。
     */
    private String content;
}
