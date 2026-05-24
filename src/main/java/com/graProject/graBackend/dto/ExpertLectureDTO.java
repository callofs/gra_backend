package com.graProject.graBackend.dto;

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
public class ExpertLectureDTO implements Serializable {
    /**
     * 讲座ID
     */

    private Long id;
    /**
     * 所属专家ID，逻辑关联t_user.id
     */

    private Long expertId;
    /**
     * 讲座标题
     */

    private String title;
    /**
     * 讲座封面图URL
     */

    private String cover;
    /**
     * 讲座简介
     */

    private String description;
    /**
     * 讲座开播时间
     */

    private LocalDateTime lectureTime;
    /**
     * 直播地址
     */

    private String liveUrl;
    /**
     * 回放地址
     */

    private String replayUrl;
    /**
     * 讲义附件URL
     */

    private String attachmentUrl;
    /**
     * 最大报名人数 0=不限制
     */

    private Integer maxSignUp;
    /**
     * 当前报名人数
     */

    private Integer signUpCount;
    /**
     * 讲座状态 0=预告中 1=直播中 2=已结束 3=已取消
     */

    private Integer status;
    /**
     * 创建时间
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

    /**
     * 当前登录用户是否已预约该讲座
     */

    private Boolean signedUp;
}
