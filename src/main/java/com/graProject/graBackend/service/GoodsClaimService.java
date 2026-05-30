package com.graProject.graBackend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graProject.graBackend.dto.GoodsClaimDTO;
import com.graProject.graBackend.dto.UserDTO;

public interface GoodsClaimService {

    GoodsClaimDTO createClaim(UserDTO loginUser, GoodsClaimDTO claimDTO);

    boolean cancelClaim(UserDTO loginUser, Long claimId);

    IPage<GoodsClaimDTO> listMyClaims(UserDTO loginUser, long page, long size);

    IPage<GoodsClaimDTO> listReceivedClaims(UserDTO loginUser, Long goodsId, long page, long size);

    boolean updateClaimStatus(UserDTO loginUser, Long claimId, Integer status);
}
