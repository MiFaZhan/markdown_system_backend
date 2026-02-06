package com.mifazhan.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 节点表
 * @TableName node
 */
@TableName(value ="node")
@Data
public class Node {
    /**
     * 节点id
     */
    @TableId(type = IdType.AUTO)
    private Long nodeId;

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    /**
     * 父节点ID 0表示项目根
     */
    private Long parentId;

    /**
     * 节点类型 0文件夹 1文件
     */
    @NotNull(message = "节点类型不能为空")
    private Integer nodeType;

    /**
     * 节点名称
     */
    @NotNull(message = "节点名称不能为空")
    private String nodeName;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime creationTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除 0否 1是
     */
    private Integer deleted;
}