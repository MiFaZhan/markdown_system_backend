package com.mifazhan.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MarkdownContentDTO {
    /**
     * 节点ID
     */
    @NotNull(message = "节点ID不能为空")
    private Long nodeId;

    /**
     * Markdown 内容
     */
    private String content;

    /**
     * 版本号
     */
    private Integer version;
}
