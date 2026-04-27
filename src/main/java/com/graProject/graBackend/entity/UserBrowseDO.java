package com.graProject.graBackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 用户浏览记录表
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_user_browse")
public class UserBrowseDO implements Serializable {
    /**
     * 浏览记录ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 浏览用户ID，逻辑关联t_user.id
     */

    @TableField("user_id")
    private Long userId;
    /**
     * 浏览类型 1=论坛贴文 2=资讯文章
     */

    @TableField("browse_type")
    private Integer browseType;
    /**
     * 关联内容ID（贴文ID/文章ID）
     */

    @TableField("relate_id")
    private Long relateId;
    /**
     * 内容标签
     */

    @TableField("tag")
    private String tag;
    /**
     * 停留时长（秒）
     */

    @TableField("stay_duration")
    private Integer stayDuration;
    /**
     * 浏览时间
     */

    @TableField("browse_time")
    private LocalDateTime browseTime;
}
