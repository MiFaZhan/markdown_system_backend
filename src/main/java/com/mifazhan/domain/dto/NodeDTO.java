package com.mifazhan.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class NodeDTO {
    /**
     * 项目ID
     */
    @ApiModelProperty(value = "项目ID")
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    /**
     * 父节点ID null表示项目根
     */
    @ApiModelProperty(value = "父节点ID")
    private Long parentId;

    /**
     * 节点类型 0文件夹 1文件
     */
    @ApiModelProperty(value = "节点类型")
    @NotNull(message = "节点类型不能为空")
    private Integer nodeType;

    /**
     * 节点名称
     */
    @ApiModelProperty(value = "节点名")
    @NotNull(message = "节点名不能为空")
    private String nodeName;


}