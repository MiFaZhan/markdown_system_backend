package com.mifazhan.domain.vo;

import java.util.Date;

import lombok.Data;

@Data
public class MarkdownFileVO {
    /**
     * 主键id
     */
    private Long markdownId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * Markdown 文件名
     */
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

}