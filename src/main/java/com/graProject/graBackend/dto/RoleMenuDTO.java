package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * RoleMenuDTO
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleMenuDTO implements Serializable {
    /**
     * id
     */

    private Long id;
    /**
     * roleId
     */

    private Long roleId;
    /**
     * menuId
     */

    private Long menuId;
}
