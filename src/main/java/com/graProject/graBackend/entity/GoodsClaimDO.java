package com.graProject.graBackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("t_goods_claim")
public class GoodsClaimDO implements Serializable {
    /**
     * 认领ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 物品ID，逻辑关联t_idle_goods.id
     */

    @TableField("goods_id")
    private Long goodsId;
    /**
     * 物品发布者ID，逻辑关联t_user.id
     */

    @TableField("publisher_id")
    private Long publisherId;
    /**
     * 认领者ID，逻辑关联t_user.id
     */

    @TableField("claimer_id")
    private Long claimerId;
    /**
     * 认领说明
     */

    @TableField("claim_desc")
    private String claimDesc;
    /**
     * 联系方式
     */

    @TableField("contact_info")
    private String contactInfo;
    /**
     * 认领状态 0=待确认 1=已同意 2=已拒绝 3=已完成 4=已取消
     */

    @TableField("status")
    private Integer status;
    /**
     * 申请时间
     */

    @TableField("create_time")
    private LocalDateTime createTime;
    /**
     * 状态更新时间
     */

    @TableField("update_time")
    private LocalDateTime updateTime;
    /**
     * 逻辑删除 0=未删除 1=已删除
     */

    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;
}
