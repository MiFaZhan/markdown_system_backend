package com.mifazhan.domain.vo;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class MarkdownContentVO {
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

    /**
     * 内容更新时间
     */
    @NotNull(message = "更新时间不能为空")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
