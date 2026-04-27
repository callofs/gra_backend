package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 系统通知表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SystemNoticeDTO implements Serializable {
    /**
     * 通知ID
     */

    private Long id;
    /**
     * 接收用户ID，逻辑关联t_user.id
     */

    private Long userId;
    /**
     * 通知类型 1=预约提醒 2=认领通知 3=贴文互动 4=系统公告
     */

    private Integer noticeType;
    /**
     * 通知内容
     */

    private String content;
    /**
     * 关联业务ID
     */

    private Long relateId;
    /**
     * 是否已读 0=未读 1=已读
     */

    private Integer isRead;
    /**
     * 发送时间
     */

    private LocalDateTime createTime;
    /**
     * 逻辑删除 0=未删除 1=已删除
     */

    private Integer isDelete;
}
