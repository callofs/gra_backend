package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 物品认领表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GoodsClaimDTO implements Serializable {
    /**
     * 认领ID
     */

    private Long id;
    /**
     * 物品ID，逻辑关联t_idle_goods.id
     */

    private Long goodsId;
    /**
     * 物品发布者ID，逻辑关联t_user.id
     */

    private Long publisherId;
    /**
     * 认领者ID，逻辑关联t_user.id
     */

    private Long claimerId;
    /**
     * 认领说明
     */

    private String claimDesc;
    /**
     * 联系方式
     */

    private String contactInfo;
    /**
     * 认领状态 0=待确认 1=已同意 2=已拒绝 3=已完成 4=已取消
     */

    private Integer status;
    /**
     * 申请时间
     */

    private LocalDateTime createTime;
    /**
     * 状态更新时间
     */

    private LocalDateTime updateTime;
    /**
     * 逻辑删除 0=未删除 1=已删除
     */

    private Integer isDelete;

    /**
     * 申请人昵称，返回给发布者查看
     */
    private String applicantName;
}
