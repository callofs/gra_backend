package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 闲置物品表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class IdleGoodsDTO implements Serializable {
    /**
     * 物品ID
     */

    private Long id;
    /**
     * 发布者ID，逻辑关联t_user.id
     */

    private Long userId;
    /**
     * 物品类型编码，逻辑关联t_dict.dict_code
     */

    private String goodsTypeCode;
    /**
     * 物品标题
     */

    private String title;
    /**
     * 物品名称
     */

    private String name;
    /**
     * 实物图片URL
     */

    private String coverImages;
    /**
     * 物品详情描述
     */

    private String description;
    /**
     * 适用年龄
     */

    private String fitAge;
    /**
     * 衣物尺码
     */

    private String size;
    /**
     * 适用季节
     */

    private String season;
    /**
     * 衣物材质
     */

    private String material;
    /**
     * 新旧程度
     */

    private String oldDegree;
    /**
     * 取件方式 1=自提 2=邮寄 3=均可
     */

    private Integer pickUpType;
    /**
     * 自提地址
     */

    private String address;
    /**
     * 浏览量
     */

    private Integer viewCount;
    /**
     * 物品状态 0=待认领 1=已认领待交付 2=已完成 3=已下架
     */

    private Integer status;
    /**
     * 发布时间
     */

    private LocalDateTime createTime;
    /**
     * 更新时间
     */

    private LocalDateTime updateTime;
    /**
     * 逻辑删除 0=未删除 1=已删除
     */

    private Integer isDelete;
}
