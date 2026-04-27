package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * UserRoleDTO
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRoleDTO implements Serializable {
    /**
     * id
     */

    private Long id;
    /**
     * userId
     */

    private Long userId;
    /**
     * roleId
     */

    private Long roleId;
}
