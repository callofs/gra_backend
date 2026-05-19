package com.graProject.graBackend.controller;

import com.graProject.graBackend.common.result.HttpCode;
import com.graProject.graBackend.common.result.HttpResult;
import com.graProject.graBackend.dto.PostLikeToggleRequestDTO;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.service.PostLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * 贴文/评论点赞接口。
 */
@RestController
@RequestMapping("/api/postLike")
@Tag(name = "点赞接口")
public class PostLikeController {

    private final PostLikeService postLikeService;

    public PostLikeController(PostLikeService postLikeService) {
        this.postLikeService = postLikeService;
    }

    /**
     * 点赞（需要登录）。
     *
     * @param request    当前请求
     * @param requestDTO 点赞参数
     * @return 操作结果
     */
    @Operation(summary = "点赞")
    @PostMapping("/like")
    public HttpResult<String> like(HttpServletRequest request, @RequestBody PostLikeToggleRequestDTO requestDTO) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO) || userDTO.getId() == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        postLikeService.like(userDTO, requestDTO == null ? null : requestDTO.getLikeType(),
                requestDTO == null ? null : requestDTO.getRelateId());
        return HttpResult.success("ok");
    }

    /**
     * 取消点赞（需要登录）。
     *
     * @param request    当前请求
     * @param requestDTO 取消点赞参数
     * @return 操作结果
     */
    @Operation(summary = "取消点赞")
    @PostMapping("/unlike")
    public HttpResult<String> unlike(HttpServletRequest request, @RequestBody PostLikeToggleRequestDTO requestDTO) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO) || userDTO.getId() == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        postLikeService.unlike(userDTO, requestDTO == null ? null : requestDTO.getLikeType(),
                requestDTO == null ? null : requestDTO.getRelateId());
        return HttpResult.success("ok");
    }

    /**
     * 查询当前用户是否已点赞（需要登录）。
     *
     * @param request  当前请求
     * @param likeType 点赞类型 1=贴文 2=评论
     * @param relateId 关联ID（贴文ID/评论ID）
     * @return 是否已点赞
     */
    @Operation(summary = "查询是否已点赞")
    @GetMapping("/status")
    public HttpResult<Boolean> hasLiked(HttpServletRequest request,
            @RequestParam("likeType") Integer likeType,
            @RequestParam("relateId") Long relateId) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO) || userDTO.getId() == null) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        return HttpResult.success(postLikeService.hasLiked(userDTO, likeType, relateId));
    }
}
