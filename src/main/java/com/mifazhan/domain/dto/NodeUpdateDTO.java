package com.mifazhan.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NodeUpdateDTO {
    /**
     * 节点id
     */
    @ApiModelProperty(value = "节点id")
    private Long nodeId;

    /**
     * 父节点ID null表示项目根
     */
    @ApiModelProperty(value = "父节点ID")
    private Long parentId;

    /**
     * 节点名称
     */
    @ApiModelProperty(value = "节点名")
    @NotNull(message = "节点名不能为空")
    private String nodeName;
}
