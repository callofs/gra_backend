package com.graProject.graBackend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.graProject.graBackend.common.exception.User.UserLoginException;
import com.graProject.graBackend.common.result.HttpCode;
import com.graProject.graBackend.dto.PrivateConversationDTO;
import com.graProject.graBackend.dto.PrivateMessageDTO;
import com.graProject.graBackend.dto.PrivateMessageSendRequestDTO;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.entity.PrivateMessageDO;
import com.graProject.graBackend.entity.UserDO;
import com.graProject.graBackend.mapper.PrivateMessageMapper;
import com.graProject.graBackend.mapper.UserMapper;
import com.graProject.graBackend.service.PrivateMessageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户私聊服务实现。
 */
@Service
public class PrivateMessageServiceImpl implements PrivateMessageService {

    /**
     * 私信 Mapper。
     */
    private final PrivateMessageMapper privateMessageMapper;

    /**
     * 用户 Mapper。
     */
    private final UserMapper userMapper;

    /**
     * 构造方法。
     *
     * @param privateMessageMapper 私信 Mapper
     * @param userMapper           用户 Mapper
     */
    public PrivateMessageServiceImpl(PrivateMessageMapper privateMessageMapper, UserMapper userMapper) {
        this.privateMessageMapper = privateMessageMapper;
        this.userMapper = userMapper;
    }

    /**
     * 发送私信。
     *
     * @param loginUser  当前登录用户
     * @param requestDTO 发送参数
     * @return 发送成功后的消息信息
     */
    @Override
    public PrivateMessageDTO sendMessage(UserDTO loginUser, PrivateMessageSendRequestDTO requestDTO) {
        validateLoginUser(loginUser);
        return saveMessage(loginUser, requestDTO);
    }

    /**
     * 保存一条私信消息。
     *
     * @param senderUser 发送者
     * @param requestDTO 发送参数
     * @return 保存后的私信消息
     */
    @Override
    public PrivateMessageDTO saveMessage(UserDTO senderUser, PrivateMessageSendRequestDTO requestDTO) {
        Long currentUserId = validateLoginUser(senderUser);
        getUserEntity(currentUserId);
        if (requestDTO == null || requestDTO.getReceiverId() == null) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "接收者用户ID不能为空");
        }
        if (currentUserId.equals(requestDTO.getReceiverId())) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "不能给自己发送消息");
        }
        Integer msgType = requestDTO.getMsgType() == null ? 1 : requestDTO.getMsgType();
        if (msgType != 1 && msgType != 2 && msgType != 3) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "消息类型不合法");
        }
        if (!StringUtils.hasText(requestDTO.getContent())) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "消息内容不能为空");
        }
        getUserEntity(requestDTO.getReceiverId());

        LocalDateTime now = LocalDateTime.now();
        PrivateMessageDO messageDO = new PrivateMessageDO();
        messageDO.setSenderId(currentUserId);
        messageDO.setReceiverId(requestDTO.getReceiverId());
        messageDO.setMsgType(msgType);
        messageDO.setContent(requestDTO.getContent().trim());
        messageDO.setIsRead(0);
        messageDO.setCreateTime(now);
        messageDO.setIsDelete(0);
        if (privateMessageMapper.insert(messageDO) <= 0) {
            throw new UserLoginException(HttpCode.FAILED, "发送消息失败");
        }
        return buildMessageDTO(messageDO);
    }

    /**
     * 获取当前用户与指定用户的聊天记录。
     *
     * @param loginUser    当前登录用户
     * @param targetUserId 对方用户 ID
     * @param page         页码
     * @param size         每页条数
     * @return 聊天记录分页结果
     */
    @Override
    public IPage<PrivateMessageDTO> listMessages(UserDTO loginUser, Long targetUserId, long page, long size) {
        Long currentUserId = validateLoginUser(loginUser);
        if (targetUserId == null) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "对方用户ID不能为空");
        }
        getUserEntity(targetUserId);
        long currentPage = page <= 0 ? 1 : page;
        long pageSize = size <= 0 ? 20 : size;

        LambdaQueryWrapper<PrivateMessageDO> wrapper = new LambdaQueryWrapper<PrivateMessageDO>()
                .and(w -> w
                        .and(x -> x.eq(PrivateMessageDO::getSenderId, currentUserId)
                                .eq(PrivateMessageDO::getReceiverId, targetUserId))
                        .or(x -> x.eq(PrivateMessageDO::getSenderId, targetUserId)
                                .eq(PrivateMessageDO::getReceiverId, currentUserId)))
                .orderByAsc(PrivateMessageDO::getCreateTime)
                .orderByAsc(PrivateMessageDO::getId);
        Page<PrivateMessageDO> pageQuery = new Page<>(currentPage, pageSize);
        IPage<PrivateMessageDO> messagePage = privateMessageMapper.selectPage(pageQuery, wrapper);
        Page<PrivateMessageDTO> resultPage = new Page<>(messagePage.getCurrent(), messagePage.getSize(),
                messagePage.getTotal());
        if (messagePage.getRecords() == null || messagePage.getRecords().isEmpty()) {
            resultPage.setRecords(Collections.emptyList());
            return resultPage;
        }
        resultPage
                .setRecords(messagePage.getRecords().stream().map(this::buildMessageDTO).collect(Collectors.toList()));
        return resultPage;
    }

    /**
     * 获取当前用户的会话列表。
     *
     * @param loginUser 当前登录用户
     * @return 会话摘要列表
     */
    @Override
    public List<PrivateConversationDTO> listConversations(UserDTO loginUser) {
        Long currentUserId = validateLoginUser(loginUser);
        LambdaQueryWrapper<PrivateMessageDO> wrapper = new LambdaQueryWrapper<PrivateMessageDO>()
                .and(w -> w.eq(PrivateMessageDO::getSenderId, currentUserId)
                        .or()
                        .eq(PrivateMessageDO::getReceiverId, currentUserId))
                .orderByDesc(PrivateMessageDO::getCreateTime)
                .orderByDesc(PrivateMessageDO::getId);
        List<PrivateMessageDO> messageDOS = privateMessageMapper.selectList(wrapper);
        if (messageDOS == null || messageDOS.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, PrivateConversationDTO> conversationMap = new LinkedHashMap<>();
        for (PrivateMessageDO messageDO : messageDOS) {
            Long targetUserId = currentUserId.equals(messageDO.getSenderId())
                    ? messageDO.getReceiverId()
                    : messageDO.getSenderId();
            PrivateConversationDTO conversationDTO = conversationMap.get(targetUserId);
            if (conversationDTO == null) {
                conversationDTO = new PrivateConversationDTO();
                conversationDTO.setTargetUserId(targetUserId);
                conversationDTO.setLastSenderId(messageDO.getSenderId());
                conversationDTO.setLastMessageType(messageDO.getMsgType());
                conversationDTO.setLastMessageContent(messageDO.getContent());
                conversationDTO.setLastMessageTime(messageDO.getCreateTime());
                conversationDTO.setUnreadCount(0);
                conversationMap.put(targetUserId, conversationDTO);
            }
            if (currentUserId.equals(messageDO.getReceiverId())
                    && targetUserId.equals(messageDO.getSenderId())
                    && Integer.valueOf(0).equals(messageDO.getIsRead())) {
                conversationDTO.setUnreadCount(conversationDTO.getUnreadCount() + 1);
            }
        }

        fillConversationUserInfo(conversationMap);
        return new ArrayList<>(conversationMap.values());
    }

    /**
     * 将当前用户与指定用户的会话消息标记为已读。
     *
     * @param loginUser    当前登录用户
     * @param targetUserId 对方用户 ID
     * @return 更新条数
     */
    @Override
    public int markConversationRead(UserDTO loginUser, Long targetUserId) {
        Long currentUserId = validateLoginUser(loginUser);
        if (targetUserId == null) {
            throw new UserLoginException(HttpCode.BAD_REQUEST, "对方用户ID不能为空");
        }
        getUserEntity(targetUserId);

        PrivateMessageDO updateDO = new PrivateMessageDO();
        updateDO.setIsRead(1);
        LambdaUpdateWrapper<PrivateMessageDO> wrapper = new LambdaUpdateWrapper<PrivateMessageDO>()
                .eq(PrivateMessageDO::getSenderId, targetUserId)
                .eq(PrivateMessageDO::getReceiverId, currentUserId)
                .eq(PrivateMessageDO::getIsRead, 0);
        return privateMessageMapper.update(updateDO, wrapper);
    }

    /**
     * 获取当前用户未读私信总数。
     *
     * @param loginUser 当前登录用户
     * @return 未读私信总数
     */
    @Override
    public long countUnreadMessages(UserDTO loginUser) {
        Long currentUserId = validateLoginUser(loginUser);
        LambdaQueryWrapper<PrivateMessageDO> wrapper = new LambdaQueryWrapper<PrivateMessageDO>()
                .eq(PrivateMessageDO::getReceiverId, currentUserId)
                .eq(PrivateMessageDO::getIsRead, 0);
        return privateMessageMapper.selectCount(wrapper);
    }

    /**
     * 校验登录用户。
     *
     * @param loginUser 当前登录用户
     * @return 当前登录用户 ID
     */
    private Long validateLoginUser(UserDTO loginUser) {
        if (loginUser == null || loginUser.getId() == null) {
            throw new UserLoginException(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage());
        }
        return loginUser.getId();
    }

    /**
     * 根据用户 ID 获取用户实体。
     *
     * @param userId 用户 ID
     * @return 用户实体
     */
    private UserDO getUserEntity(Long userId) {
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<UserDO>()
                .eq(UserDO::getId, userId)
                .eq(UserDO::getIsDelete, 0)
                .last("limit 1");
        UserDO userDO = userMapper.selectOne(wrapper);
        if (userDO == null) {
            throw new UserLoginException(HttpCode.NOT_FOUND, "用户不存在");
        }
        return userDO;
    }

    /**
     * 填充会话中的对方用户昵称。
     *
     * @param conversationMap 会话映射
     */
    private void fillConversationUserInfo(Map<Long, PrivateConversationDTO> conversationMap) {
        Set<Long> userIds = conversationMap.keySet();
        if (userIds.isEmpty()) {
            return;
        }
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<UserDO>()
                .in(UserDO::getId, userIds)
                .eq(UserDO::getIsDelete, 0);
        Map<Long, UserDO> userMap = userMapper.selectList(wrapper).stream()
                .collect(Collectors.toMap(UserDO::getId, item -> item));
        for (Map.Entry<Long, PrivateConversationDTO> entry : conversationMap.entrySet()) {
            UserDO userDO = userMap.get(entry.getKey());
            if (userDO != null) {
                entry.getValue().setTargetNickname(userDO.getNickname());
            }
        }
    }

    /**
     * 将私信实体转换为 DTO。
     *
     * @param messageDO 私信实体
     * @return 私信 DTO
     */
    private PrivateMessageDTO buildMessageDTO(PrivateMessageDO messageDO) {
        PrivateMessageDTO messageDTO = new PrivateMessageDTO();
        messageDTO.setId(messageDO.getId());
        messageDTO.setSenderId(messageDO.getSenderId());
        messageDTO.setReceiverId(messageDO.getReceiverId());
        messageDTO.setMsgType(messageDO.getMsgType());
        messageDTO.setContent(messageDO.getContent());
        messageDTO.setIsRead(messageDO.getIsRead());
        messageDTO.setCreateTime(messageDO.getCreateTime());
        messageDTO.setIsDelete(messageDO.getIsDelete());
        return messageDTO;
    }
}
