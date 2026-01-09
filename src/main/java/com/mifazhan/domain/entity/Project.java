package com.mifazhan.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

/**
 * 
 * @TableName project
 */
@TableName(value ="project")
@Data
public class Project {
    /**
     * 项目id
     */
    @TableId(type = IdType.AUTO)
    private Integer projectId;

    /**
     * 项目姓名
     */
    @NotBlank(message = "项目名称不能为空")
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

    /**
     * 是否删除 0否 1是
     */
    private Integer deleted;
}