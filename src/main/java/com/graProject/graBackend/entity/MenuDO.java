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
 * MenuDO
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_menu")
public class MenuDO implements Serializable {
    /**
     * id
     */

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * menuName
     */

    @TableField("menu_name")
    private String menuName;
    /**
     * parentId
     */

    @TableField("parent_id")
    private Long parentId;
    /**
     * 权限标识 system:user:list
     */

    @TableField("perms")
    private String perms;
}
