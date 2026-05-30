package com.graProject.graBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpertCertificationMaterialDTO implements Serializable {
    private Long userId;
    private String username;
    private String nickname;
    private String certificationMaterials;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
