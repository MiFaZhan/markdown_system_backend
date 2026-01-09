package com.mifazhan.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectVO {
    /**
     * 项目姓名
     */
    private String projectName;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 图标
     */
    private String icon;

    /**
     * 所属用户ID
     */
    private Integer userId;

    /**
     * 创建时间
     */
    private Date creationTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
