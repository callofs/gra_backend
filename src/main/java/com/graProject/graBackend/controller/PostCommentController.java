package com.graProject.graBackend.controller;

import com.graProject.graBackend.common.result.HttpCode;
import com.graProject.graBackend.common.result.HttpResult;
import com.graProject.graBackend.dto.PostCommentCreateRequestDTO;
import com.graProject.graBackend.dto.PostCommentDTO;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.service.PostCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 贴文评论/回复接口。
 */
@RestController
@RequestMapping("/api/forumPost/comments")
@Tag(name = "贴文评论接口")
public class PostCommentController {

    private final PostCommentService postCommentService;

    public PostCommentController(PostCommentService postCommentService) {
        this.postCommentService = postCommentService;
    }

    /**
     * 获取贴文一级评论列表。
     *
     * @param postId 贴文 ID
     * @return 一级评论列表
     */
    @Operation(summary = "获取贴文一级评论列表")
    @GetMapping("/getComments")
    public HttpResult<List<PostCommentDTO>> listComments(@RequestParam("postId") Long postId) {
        return HttpResult.success(postCommentService.listCommentsByPostId(postId));
    }

    /**
     * 获取评论的回复列表。
     *
     * @param parentCommentId 父评论 ID
     * @return 回复列表
     */
    @Operation(summary = "获取评论回复列表")
    @GetMapping("/getReplies")
    public HttpResult<List<PostCommentDTO>> listReplies(@RequestParam("parentCommentId") Long parentCommentId) {
        return HttpResult.success(postCommentService.listRepliesByParentId(parentCommentId));
    }

    /**
     * 发表评论/回复（需要登录）。
     *
     * @param request    当前请求（用于读取登录用户信息）
     * @param requestDTO 发布参数
     * @return 新增的评论
     */
    @Operation(summary = "发表评论/回复")
    @PostMapping("/makeComment")
    public HttpResult<PostCommentDTO> createComment(HttpServletRequest request,
            @RequestBody PostCommentCreateRequestDTO requestDTO) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO) || userDTO.getId() == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        return HttpResult.success(postCommentService.createComment(userDTO, requestDTO));
    }
}
