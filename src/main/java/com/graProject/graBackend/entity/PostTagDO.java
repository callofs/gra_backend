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
 * 贴文标签表
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_post_tag")
public class PostTagDO implements Serializable {
    /**
     * 标签记录ID
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 贴文ID，逻辑关联t_forum_post.id
     */

    @TableField("post_id")
    private Long postId;
    /**
     * 标签名称
     */

    @TableField("tag_name")
    private String tagName;
    /**
     * 创建时间
     */

    @TableField("create_time")
    private LocalDateTime createTime;
}
