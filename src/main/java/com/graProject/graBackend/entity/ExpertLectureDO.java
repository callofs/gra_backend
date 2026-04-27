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
 * 专家讲座表
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_expert_lecture")
public class ExpertLectureDO implements Serializable {
    /**
     * 讲座ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 所属专家ID，逻辑关联t_user.id
     */

    @TableField("expert_id")
    private Long expertId;
    /**
     * 讲座标题
     */

    @TableField("title")
    private String title;
    /**
     * 讲座封面图URL
     */

    @TableField("cover")
    private String cover;
    /**
     * 讲座简介
     */

    @TableField("description")
    private String description;
    /**
     * 讲座开播时间
     */

    @TableField("lecture_time")
    private LocalDateTime lectureTime;
    /**
     * 直播地址
     */

    @TableField("live_url")
    private String liveUrl;
    /**
     * 回放地址
     */

    @TableField("replay_url")
    private String replayUrl;
    /**
     * 讲义附件URL
     */

    @TableField("attachment_url")
    private String attachmentUrl;
    /**
     * 最大报名人数 0=不限制
     */

    @TableField("max_sign_up")
    private Integer maxSignUp;
    /**
     * 当前报名人数
     */

    @TableField("sign_up_count")
    private Integer signUpCount;
    /**
     * 讲座状态 0=预告中 1=直播中 2=已结束 3=已取消
     */

    @TableField("status")
    private Integer status;
    /**
     * 创建时间
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
