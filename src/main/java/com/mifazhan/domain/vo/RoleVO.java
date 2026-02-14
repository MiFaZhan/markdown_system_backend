package com.mifazhan.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleVO {
    private Long roleId;
    private String roleName;
    private String roleCode;
    private List<String> permissions;
    private String description;
}
