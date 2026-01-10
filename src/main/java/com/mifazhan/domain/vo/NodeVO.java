package com.mifazhan.domain.vo;

import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NodeVO {
    /**
     * 节点id
     */
    private Long nodeId;

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    /**
     * 父节点ID null表示项目根
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
    private LocalDateTime creationTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}