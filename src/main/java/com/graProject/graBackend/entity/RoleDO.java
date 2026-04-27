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
 * RoleDO
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_role")
public class RoleDO implements Serializable {
    /**
     * id
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 角色名称
     */

    @TableField("role_name")
    private String roleName;
    /**
     * 角色标识 admin/expert/user
     */

    @TableField("role_code")
    private String roleCode;
    /**
     * 状态
     */

    @TableField("status")
    private Integer status;
}
