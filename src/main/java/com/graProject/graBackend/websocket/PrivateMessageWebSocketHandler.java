package com.graProject.graBackend.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graProject.graBackend.dto.PrivateMessageDTO;
import com.graProject.graBackend.dto.PrivateMessageSendRequestDTO;
import com.graProject.graBackend.dto.PrivateMessageWsRequestDTO;
import com.graProject.graBackend.dto.PrivateMessageWsResponseDTO;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.service.PrivateMessageService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * 私聊 WebSocket 处理器。
 */
@Component
public class PrivateMessageWebSocketHandler extends TextWebSocketHandler {

    /**
     * 私聊服务。
     */
    private final PrivateMessageService privateMessageService;

    /**
     * 在线会话管理器。
     */
    private final PrivateMessageSessionManager sessionManager;

    /**
     * JSON 序列化工具。
     */
    private final ObjectMapper objectMapper;

    /**
     * 构造方法。
     *
     * @param privateMessageService 私聊服务
     * @param sessionManager        在线会话管理器
     * @param objectMapper          JSON 序列化工具
     */
    public PrivateMessageWebSocketHandler(PrivateMessageService privateMessageService,
            PrivateMessageSessionManager sessionManager,
            ObjectMapper objectMapper) {
        this.privateMessageService = privateMessageService;
        this.sessionManager = sessionManager;
        this.objectMapper = objectMapper;
    }

    /**
     * 建立连接后注册在线用户。
     *
     * @param session 当前会话
     * @throws Exception 异常
     */
    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        UserDTO loginUser = getLoginUser(session);
        if (loginUser == null || loginUser.getId() == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("未登录"));
            return;
        }
        sessionManager.register(loginUser.getId(), session);
        sendToSession(session, PrivateMessageWsResponseDTO.builder()
                .event("connected")
                .message("连接成功")
                .unreadCount(privateMessageService.countUnreadMessages(loginUser))
                .build());
    }

    /**
     * 处理文本消息。
     *
     * @param session 当前会话
     * @param message 文本消息
     * @throws Exception 异常
     */
    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        UserDTO loginUser = getLoginUser(session);
        if (loginUser == null || loginUser.getId() == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("未登录"));
            return;
        }
        if (message == null || !StringUtils.hasText(message.getPayload())) {
            return;
        }
        PrivateMessageWsRequestDTO requestDTO = objectMapper.readValue(message.getPayload(),
                PrivateMessageWsRequestDTO.class);
        if (requestDTO == null || !"send".equalsIgnoreCase(requestDTO.getAction())) {
            sendToSession(session, PrivateMessageWsResponseDTO.builder()
                    .event("error")
                    .message("暂不支持的消息动作")
                    .build());
            return;
        }

        PrivateMessageSendRequestDTO sendRequestDTO = new PrivateMessageSendRequestDTO();
        sendRequestDTO.setReceiverId(requestDTO.getReceiverId());
        sendRequestDTO.setMsgType(requestDTO.getMsgType());
        sendRequestDTO.setContent(requestDTO.getContent());
        PrivateMessageDTO savedMessage = privateMessageService.saveMessage(loginUser, sendRequestDTO);

        PrivateMessageWsResponseDTO senderResponse = PrivateMessageWsResponseDTO.builder()
                .event("message")
                .message("发送成功")
                .data(savedMessage)
                .unreadCount(privateMessageService.countUnreadMessages(loginUser))
                .build();
        sendToSession(session, senderResponse);

        WebSocketSession receiverSession = sessionManager.getSession(savedMessage.getReceiverId());
        if (receiverSession != null && receiverSession.isOpen()) {
            UserDTO receiverUser = getLoginUser(receiverSession);
            sendToSession(receiverSession, PrivateMessageWsResponseDTO.builder()
                    .event("message")
                    .message("收到新消息")
                    .data(savedMessage)
                    .unreadCount(receiverUser == null ? null : privateMessageService.countUnreadMessages(receiverUser))
                    .build());
        }
    }

    /**
     * 连接关闭后移除在线用户。
     *
     * @param session 当前会话
     * @param status  关闭状态
     * @throws Exception 异常
     */
    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        UserDTO loginUser = getLoginUser(session);
        if (loginUser != null) {
            sessionManager.remove(loginUser.getId());
        }
        super.afterConnectionClosed(session, status);
    }

    /**
     * 处理传输异常。
     *
     * @param session   当前会话
     * @param exception 异常
     * @throws Exception 异常
     */
    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) throws Exception {
        UserDTO loginUser = getLoginUser(session);
        if (loginUser != null) {
            sessionManager.remove(loginUser.getId());
        }
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    /**
     * 获取当前会话中的登录用户。
     *
     * @param session WebSocket 会话
     * @return 登录用户
     */
    private UserDTO getLoginUser(WebSocketSession session) {
        Object loginUser = session.getAttributes().get("loginUser");
        if (loginUser instanceof UserDTO userDTO) {
            return userDTO;
        }
        return null;
    }

    /**
     * 向指定会话发送消息。
     *
     * @param session     WebSocket 会话
     * @param responseDTO 响应对象
     * @throws Exception 异常
     */
    private void sendToSession(WebSocketSession session, PrivateMessageWsResponseDTO responseDTO) throws Exception {
        if (session == null || !session.isOpen() || responseDTO == null) {
            return;
        }
        String payload = objectMapper.writeValueAsString(responseDTO);
        session.sendMessage(new TextMessage(payload));
    }
}
