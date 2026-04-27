package com.graProject.graBackend.dto;

import lombok.Data;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * MenuDTO
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MenuDTO implements Serializable {
    /**
     * id
     */

    private Long id;
    /**
     * menuName
     */

    private String menuName;
    /**
     * parentId
     */

    private Long parentId;
    /**
     * 权限标识 system:user:list
     */

    private String perms;
}
