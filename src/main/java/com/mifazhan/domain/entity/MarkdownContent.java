package com.mifazhan.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Markdown内容表
 * @TableName markdown_content
 */
@Data
@TableName(value = "markdown_content")
public class MarkdownContent {
    /**
     * 节点ID
     */
    @TableId
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
    private Integer version;

    /**
     * 内容更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除 0否 1是
     */
    @TableLogic
    private Integer deleted;
}
