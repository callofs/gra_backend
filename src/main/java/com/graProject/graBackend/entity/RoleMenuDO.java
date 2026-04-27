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
 * RoleMenuDO
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_role_menu")
public class RoleMenuDO implements Serializable {
    /**
     * id
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * roleId
     */

    @TableField("role_id")
    private Long roleId;
    /**
     * menuId
     */

    @TableField("menu_id")
    private Long menuId;
}
