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
 * 系统通知表
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_system_notice")
public class SystemNoticeDO implements Serializable {
    /**
     * 通知ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 接收用户ID，逻辑关联t_user.id
     */

    @TableField("user_id")
    private Long userId;
    /**
     * 通知类型 1=预约提醒 2=认领通知 3=贴文互动 4=系统公告
     */

    @TableField("notice_type")
    private Integer noticeType;
    /**
     * 通知内容
     */

    @TableField("content")
    private String content;
    /**
     * 关联业务ID
     */

    @TableField("relate_id")
    private Long relateId;
    /**
     * 是否已读 0=未读 1=已读
     */

    @TableField("is_read")
    private Integer isRead;
    /**
     * 发送时间
     */

    @TableField("create_time")
    private LocalDateTime createTime;
    /**
     * 逻辑删除 0=未删除 1=已删除
     */

    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;
}
