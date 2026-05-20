package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 发送私信请求参数。
 */
@Data
public class PrivateMessageSendRequestDTO implements Serializable {

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
