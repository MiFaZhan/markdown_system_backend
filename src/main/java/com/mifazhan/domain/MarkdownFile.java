package com.mifazhan.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
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
    private Long id;

    /**
     * 所属用户ID
     */
    private Long userId;

    /**
     * Markdown 文件名
     */
    private String fileName;

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
     * 是否删除 0否 1是
     */
    private Integer deleted;
}