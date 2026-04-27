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
 * 闲置物品表
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_idle_goods")
public class IdleGoodsDO implements Serializable {
    /**
     * 物品ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 发布者ID，逻辑关联t_user.id
     */

    @TableField("user_id")
    private Long userId;
    /**
     * 物品类型编码，逻辑关联t_dict.dict_code
     */

    @TableField("goods_type_code")
    private String goodsTypeCode;
    /**
     * 物品标题
     */

    @TableField("title")
    private String title;
    /**
     * 物品名称
     */

    @TableField("name")
    private String name;
    /**
     * 实物图片URL
     */

    @TableField("cover_images")
    private String coverImages;
    /**
     * 物品详情描述
     */

    @TableField("description")
    private String description;
    /**
     * 适用年龄
     */

    @TableField("fit_age")
    private String fitAge;
    /**
     * 衣物尺码
     */

    @TableField("size")
    private String size;
    /**
     * 适用季节
     */

    @TableField("season")
    private String season;
    /**
     * 衣物材质
     */

    @TableField("material")
    private String material;
    /**
     * 新旧程度
     */

    @TableField("old_degree")
    private String oldDegree;
    /**
     * 取件方式 1=自提 2=邮寄 3=均可
     */

    @TableField("pick_up_type")
    private Integer pickUpType;
    /**
     * 自提地址
     */

    @TableField("address")
    private String address;
    /**
     * 浏览量
     */

    @TableField("view_count")
    private Integer viewCount;
    /**
     * 物品状态 0=待认领 1=已认领待交付 2=已完成 3=已下架
     */

    @TableField("status")
    private Integer status;
    /**
     * 发布时间
     */

    @TableField("create_time")
    private LocalDateTime createTime;
    /**
     * 更新时间
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
