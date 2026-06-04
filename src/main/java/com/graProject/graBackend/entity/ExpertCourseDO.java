package com.graProject.graBackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 专家课程表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_expert_course")
public class ExpertCourseDO implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 专家用户ID */
    @TableField("expert_id")
    private Long expertId;

    /** 课程标题 */
    @TableField("title")
    private String title;

    /** 课程封面 */
    @TableField("cover_url")
    private String coverUrl;

    /** 课程简介 */
    @TableField("course_desc")
    private String courseDesc;

    /** 课程文件 OSS objectKey */
    @TableField("video_object_key")
    private String videoObjectKey;

    /** 课程时长（秒） */
    @TableField("duration_seconds")
    private Integer durationSeconds;

    /** 审核状态 0=待审核 1=审核通过 2=审核驳回 3=已下架 */
    @TableField("audit_status")
    private Integer auditStatus;

    /** 审核备注 */
    @TableField("audit_comment")
    private String auditComment;

    /** 审核时间 */
    @TableField("audit_time")
    private LocalDateTime auditTime;

    /** 审核人ID */
    @TableField("audit_by")
    private Long auditBy;

    /** 浏览次数 */
    @TableField("view_count")
    private Integer viewCount;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;
}
