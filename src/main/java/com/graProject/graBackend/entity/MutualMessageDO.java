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
 * 互助站内信表
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_mutual_message")
public class MutualMessageDO implements Serializable {
    /**
     * 消息ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 关联物品ID，逻辑关联t_idle_goods.id
     */

    @TableField("goods_id")
    private Long goodsId;
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
