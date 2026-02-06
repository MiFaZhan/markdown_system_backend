package com.mifazhan.domain.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 节点树形结构VO - 包含项目信息和节点树
 * @author MIFAZHAN
 */
@Data
public class NodeTreeVO {
    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 项目名称
     */
    private String projectName;


    /**
     * 根节点列表
     */
    private List<NodeItemVO> rootNodes;

    /**
     * 节点总数统计
     */
    private Integer totalNodes;

    /**
     * 文件数统计
     */
    private Integer fileCount;

    /**
     * 文件夹数统计
     */
    private Integer folderCount;

    /**
     * 构造函数，初始化rootNodes列表
     */
    public NodeTreeVO() {
        this.rootNodes = new ArrayList<>();
    }

    /**
     * 节点项VO - 树形结构中的单个节点
     */
    @Data
    public static class NodeItemVO {
        /**
         * 节点id
         */
        private Long nodeId;

        /**
         * 父节点ID 0表示项目根
         */
        private Long parentId;

        /**
         * 节点类型 0文件夹 1文件
         */
        private Integer nodeType;

        /**
         * 节点名称
         */
        private String nodeName;

        /**
         * 创建时间
         */
        private LocalDateTime creationTime;

        /**
         * 更新时间
         */
        private LocalDateTime updateTime;

        /**
         * 子节点列表
         */
        private List<NodeItemVO> children;

        /**
         * 构造函数，初始化children列表
         */
        public NodeItemVO() {
            this.children = new ArrayList<>();
        }
    }
}