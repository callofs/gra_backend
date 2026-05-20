package com.graProject.graBackend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graProject.graBackend.dto.PrivateConversationDTO;
import com.graProject.graBackend.dto.PrivateMessageDTO;
import com.graProject.graBackend.dto.PrivateMessageSendRequestDTO;
import com.graProject.graBackend.dto.UserDTO;

import java.util.List;

/**
 * 用户私聊服务。
 */
public interface PrivateMessageService {

    /**
     * 发送私信。
     *
     * @param loginUser  当前登录用户
     * @param requestDTO 发送参数
     * @return 发送成功后的消息信息
     */
    PrivateMessageDTO sendMessage(UserDTO loginUser, PrivateMessageSendRequestDTO requestDTO);

    /**
     * 获取当前用户与指定用户的聊天记录。
     *
     * @param loginUser    当前登录用户
     * @param targetUserId 对方用户 ID
     * @param page         页码
     * @param size         每页条数
     * @return 聊天记录分页结果
     */
    IPage<PrivateMessageDTO> listMessages(UserDTO loginUser, Long targetUserId, long page, long size);

    /**
     * 获取当前用户的会话列表。
     *
     * @param loginUser 当前登录用户
     * @return 会话摘要列表
     */
    List<PrivateConversationDTO> listConversations(UserDTO loginUser);

    /**
     * 将当前用户与指定用户的会话消息标记为已读。
     *
     * @param loginUser    当前登录用户
     * @param targetUserId 对方用户 ID
     * @return 更新条数
     */
    int markConversationRead(UserDTO loginUser, Long targetUserId);

    /**
     * 获取当前用户未读私信总数。
     *
     * @param loginUser 当前登录用户
     * @return 未读私信总数
     */
    long countUnreadMessages(UserDTO loginUser);

    /**
     * 保存一条私信消息。
     *
     * @param senderUser 发送者
     * @param requestDTO 发送参数
     * @return 保存后的私信消息
     */
    PrivateMessageDTO saveMessage(UserDTO senderUser, PrivateMessageSendRequestDTO requestDTO);
}
