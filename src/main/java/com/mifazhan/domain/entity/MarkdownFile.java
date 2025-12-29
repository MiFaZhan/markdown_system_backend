package com.mifazhan.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Markdown 文件表
 * @TableName markdown_file
 */
@TableName(value ="markdown_file")
@Data
public class MarkdownFile {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long markdownId;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * Markdown 文件名
     */
    @NotNull(message = "文件名不能为空")
    private String markdownName;

    /**
     * Markdown 文件内容
     */
    private String markdownContent;

    /**
     * 创建时间
     */
    private Date creationTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 备注
     */
    private String remark;

    /**
     * 逻辑删除 0否 1是
     */
    private Integer deleted;
}