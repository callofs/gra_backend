package com.graProject.graBackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graProject.graBackend.common.result.HttpCode;
import com.graProject.graBackend.common.result.HttpResult;
import com.graProject.graBackend.dto.PrivateConversationDTO;
import com.graProject.graBackend.dto.PrivateMessageDTO;
import com.graProject.graBackend.dto.PrivateMessageSendRequestDTO;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.service.PrivateMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户私聊接口。
 */
@RestController
@RequestMapping("/api/privateMessage")
@Tag(name = "用户私聊接口")
public class PrivateMessageController {

    /**
     * 用户私聊服务。
     */
    private final PrivateMessageService privateMessageService;

    /**
     * 构造方法。
     *
     * @param privateMessageService 用户私聊服务
     */
    public PrivateMessageController(PrivateMessageService privateMessageService) {
        this.privateMessageService = privateMessageService;
    }

    /**
     * 发送私信。
     *
     * @param request    当前请求
     * @param requestDTO 发送参数
     * @return 新发送的私信
     */
    @Operation(summary = "发送私信")
    @PostMapping("/send")
    public HttpResult<PrivateMessageDTO> sendMessage(HttpServletRequest request,
            @RequestBody PrivateMessageSendRequestDTO requestDTO) {
        UserDTO userDTO = getLoginUser(request);
        if (userDTO == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        return HttpResult.success(privateMessageService.sendMessage(userDTO, requestDTO));
    }

    /**
     * 获取当前用户的会话列表。
     *
     * @param request 当前请求
     * @return 会话列表
     */
    @Operation(summary = "获取私聊会话列表")
    @GetMapping("/conversations")
    public HttpResult<List<PrivateConversationDTO>> listConversations(HttpServletRequest request) {
        UserDTO userDTO = getLoginUser(request);
        if (userDTO == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        return HttpResult.success(privateMessageService.listConversations(userDTO));
    }

    /**
     * 获取当前用户与指定用户的聊天记录。
     *
     * @param request      当前请求
     * @param targetUserId 对方用户 ID
     * @param page         页码
     * @param size         每页条数
     * @return 聊天记录分页结果
     */
    @Operation(summary = "获取聊天记录")
    @GetMapping("/messages")
    public HttpResult<IPage<PrivateMessageDTO>> listMessages(HttpServletRequest request,
            @RequestParam("targetUserId") Long targetUserId,
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "20") long size) {
        UserDTO userDTO = getLoginUser(request);
        if (userDTO == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        return HttpResult.success(privateMessageService.listMessages(userDTO, targetUserId, page, size));
    }

    /**
     * 获取当前用户未读私信总数。
     *
     * @param request 当前请求
     * @return 未读私信总数
     */
    @Operation(summary = "获取未读私信总数")
    @GetMapping("/unread/count")
    public HttpResult<Long> countUnreadMessages(HttpServletRequest request) {
        UserDTO userDTO = getLoginUser(request);
        if (userDTO == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        return HttpResult.success(privateMessageService.countUnreadMessages(userDTO));
    }

    /**
     * 将指定会话标记为已读。
     *
     * @param request      当前请求
     * @param targetUserId 对方用户 ID
     * @return 已更新的消息条数
     */
    @Operation(summary = "将会话标记为已读")
    @PostMapping("/read/{targetUserId}")
    public HttpResult<Integer> markConversationRead(HttpServletRequest request,
            @PathVariable("targetUserId") Long targetUserId) {
        UserDTO userDTO = getLoginUser(request);
        if (userDTO == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        return HttpResult.success(privateMessageService.markConversationRead(userDTO, targetUserId));
    }

    /**
     * 获取当前登录用户。
     *
     * @param request 当前请求
     * @return 当前登录用户；未登录时返回 null
     */
    private UserDTO getLoginUser(HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO) || userDTO.getId() == null) {
            return null;
        }
        return userDTO;
    }
}
