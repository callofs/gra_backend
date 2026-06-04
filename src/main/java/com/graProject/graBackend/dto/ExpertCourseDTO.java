package com.graProject.graBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpertCourseDTO implements Serializable {

    private Long id;
    private Long expertId;
    private String title;
    private String coverUrl;
    private String courseDesc;
    private String videoObjectKey;
    private Integer durationSeconds;
    /** 审核状态 0=待审核 1=通过 2=驳回 3=下架 */
    private Integer auditStatus;
    private String auditComment;
    private LocalDateTime auditTime;
    private Long auditBy;
    private Integer viewCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /** 专家昵称、头像等扩展字段 */
    private String expertNickname;
    private byte[] expertAvatar;
}
