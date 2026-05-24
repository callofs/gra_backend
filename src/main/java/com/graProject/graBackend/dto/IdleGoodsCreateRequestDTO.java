package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 发布闲置物品请求参数。
 */
@Data
public class IdleGoodsCreateRequestDTO implements Serializable {

    /**
     * 物品类型编码。
     */
    private String goodsTypeCode;

    /**
     * 物品标题。
     */
    private String title;

    /**
     * 物品名称。
     */
    private String name;

    /**
     * 实物图片 URL。
     */
    private String coverImages;

    /**
     * 物品详情描述。
     */
    private String description;

    /**
     * 适用年龄。
     */
    private String fitAge;

    /**
     * 衣物尺码。
     */
    private String size;

    /**
     * 适用季节。
     */
    private String season;

    /**
     * 衣物材质。
     */
    private String material;

    /**
     * 新旧程度。
     */
    private String oldDegree;

    /**
     * 取件方式 1=自提 2=邮寄 3=均可。
     */
    private Integer pickUpType;

    /**
     * 自提地址。
     */
    private String address;
}
