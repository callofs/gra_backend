package com.graProject.graBackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graProject.graBackend.common.result.HttpCode;
import com.graProject.graBackend.common.result.HttpResult;
import com.graProject.graBackend.dto.GoodsClaimDTO;
import com.graProject.graBackend.dto.UserDTO;
import com.graProject.graBackend.service.GoodsClaimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/idleGoods/claim")
@Tag(name = "闲置物品认领接口")
public class GoodsClaimController {

    private final GoodsClaimService goodsClaimService;

    public GoodsClaimController(GoodsClaimService goodsClaimService) {
        this.goodsClaimService = goodsClaimService;
    }

    @Operation(summary = "提交认领申请")
    @PostMapping("/apply")
    public HttpResult<GoodsClaimDTO> createClaim(@RequestBody GoodsClaimDTO claimDTO, HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO)) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        try {
            return HttpResult.success(goodsClaimService.createClaim(userDTO, claimDTO));
        } catch (RuntimeException e) {
            return HttpResult.fail(e.getMessage());
        }
    }

    @Operation(summary = "取消认领申请")
    @PostMapping("/cancel/{claimId}")
    public HttpResult<String> cancelClaim(@PathVariable("claimId") Long claimId, HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO)) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        try {
            boolean success = goodsClaimService.cancelClaim(userDTO, claimId);
            return success ? HttpResult.success("取消成功") : HttpResult.fail("取消失败");
        } catch (RuntimeException e) {
            return HttpResult.fail(e.getMessage());
        }
    }

    @Operation(summary = "获取我的认领记录")
    @GetMapping("/my")
    public HttpResult<IPage<GoodsClaimDTO>> listMyClaims(
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "10") long size,
            HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO)) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        try {
            return HttpResult.success(goodsClaimService.listMyClaims(userDTO, page, size));
        } catch (RuntimeException e) {
            return HttpResult.fail(e.getMessage());
        }
    }

    @Operation(summary = "获取我收到的认领申请")
    @GetMapping("/received")
    public HttpResult<IPage<GoodsClaimDTO>> listReceivedClaims(
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "10") long size,
            @RequestParam(value = "goodsId", required = false) Long goodsId,
            HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO)) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        try {
            return HttpResult.success(goodsClaimService.listReceivedClaims(userDTO, goodsId, page, size));
        } catch (RuntimeException e) {
            return HttpResult.fail(e.getMessage());
        }
    }

    @Operation(summary = "处理认领状态")
    @PutMapping("/status/{claimId}")
    public HttpResult<String> updateClaimStatus(@PathVariable("claimId") Long claimId,
            @RequestParam("status") Integer status,
            HttpServletRequest request) {
        Object loginUser = request.getAttribute("loginUser");
        if (!(loginUser instanceof UserDTO userDTO)) {
            return HttpResult.of(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMessage(), null);
        }
        try {
            boolean success = goodsClaimService.updateClaimStatus(userDTO, claimId, status);
            return success ? HttpResult.success("处理成功") : HttpResult.fail("处理失败");
        } catch (RuntimeException e) {
            return HttpResult.fail(e.getMessage());
        }
    }
}
