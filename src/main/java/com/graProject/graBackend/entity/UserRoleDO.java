package com.graProject.graBackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * UserRoleDO
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_user_role")
public class UserRoleDO implements Serializable {
    /**
     * id
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * userId
     */

    @TableField("user_id")
    private Long userId;
    /**
     * roleId
     */

    @TableField("role_id")
    private Long roleId;
}
