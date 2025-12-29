package com.mifazhan.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

import lombok.Data;

@Data
public class MarkdownFileDTO {

    /**
     * 所属用户ID
     */
    @ApiModelProperty(value = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * Markdown 文件名
     */
    @ApiModelProperty(value = "文件名")
    @NotNull(message = "文件名不能为空")
    private String markdownName;

    /**
     * Markdown 文件内容
     */
    @ApiModelProperty(value = "文件内容")
    private String markdownContent;

    /**
     * 版本号
     */
    @ApiModelProperty(value = "版本号")
    private Integer version;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
}