package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * RoleDTO
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleDTO implements Serializable {
    /**
     * id
     */

    private Long id;
    /**
     * 角色名称
     */

    private String roleName;
    /**
     * 角色标识 admin/expert/user
     */

    private String roleCode;
    /**
     * 状态
     */

    private Integer status;
}
