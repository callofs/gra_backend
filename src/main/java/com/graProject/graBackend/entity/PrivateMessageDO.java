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
 * 私信表
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_private_message")
public class PrivateMessageDO implements Serializable {
    /**
     * 私信ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 发送者ID，逻辑关联t_user.id
     */

    @TableField("sender_id")
    private Long senderId;
    /**
     * 接收者ID，逻辑关联t_user.id
     */

    @TableField("receiver_id")
    private Long receiverId;
    /**
     * 消息类型 1=文本 2=图片 3=表情
     */

    @TableField("msg_type")
    private Integer msgType;
    /**
     * 消息内容
     */

    @TableField("content")
    private String content;
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
