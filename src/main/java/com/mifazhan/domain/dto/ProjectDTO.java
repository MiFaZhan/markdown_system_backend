package com.mifazhan.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class ProjectDTO {
    /**
     * 项目姓名
     */
    @NotBlank(message = "项目名称不能为空")
    private String projectName;

    /**
     * 所属用户ID
     */
    private Integer userId;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 图标
     */
    private String icon;
}
