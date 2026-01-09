package com.mifazhan.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectUpdateDTO {
    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空")
    private Integer projectId;
    /**
     * 项目姓名
     */
    @NotBlank(message = "项目名称不能为空")
    private String projectName;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 图标
     */
    private String icon;
}
