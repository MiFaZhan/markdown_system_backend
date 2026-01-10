package com.mifazhan.domain.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import jakarta.validation.constraints.NotNull;

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
    @Version
    @NotNull(message = "版本号不能为空")
    private Integer version;
}
