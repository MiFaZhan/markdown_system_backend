package com.mifazhan.service.helper;

import com.mifazhan.domain.convert.NodeConvert;
import com.mifazhan.domain.entity.Node;
import com.mifazhan.domain.vo.NodeTreeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NodeTreeHelper: 负责构建节点树形结构的逻辑，避免 Service 之间的循环依赖
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NodeTreeHelper {

    private final NodeConvert nodeConvert;

    /**
     * 构建树形结构
     * @param allNodes 所有节点列表（已按创建时间排序）
     * @return 树形结构的根节点列表
     */
    public List<NodeTreeVO.NodeItemVO> buildNodeTree(List<Node> allNodes) {
        // 1. 创建Map，key是nodeId，value是对应的NodeItemVO
        Map<Long, NodeTreeVO.NodeItemVO> nodeMap = new HashMap<>();
        
        // 2. 先将所有节点转换为NodeItemVO并放入Map
        for (Node node : allNodes) {
            NodeTreeVO.NodeItemVO itemVO = nodeConvert.toNodeItemVO(node);
            nodeMap.put(node.getNodeId(), itemVO);
        }
        
        // 3. 建立父子关系
        List<NodeTreeVO.NodeItemVO> rootNodes = new ArrayList<>();
        for (Node node : allNodes) {
            NodeTreeVO.NodeItemVO currentNode = nodeMap.get(node.getNodeId());
            
            if (node.getParentId() == 0) {
                // 根节点
                rootNodes.add(currentNode);
            } else {
                // 子节点，添加到父节点的children中
                NodeTreeVO.NodeItemVO parentNode = nodeMap.get(node.getParentId());
                if (parentNode != null) {
                    parentNode.getChildren().add(currentNode);
                } else {
                    // 如果找不到父节点，可能是数据不一致，将其作为根节点处理
                    // 或者这里可以记录日志，但在Helper中简单处理
                    rootNodes.add(currentNode);
                }
            }
        }
        
        return rootNodes;
    }

    /**
     * 递归统计子树信息
     */
    public int[] countSubTree(NodeTreeVO.NodeItemVO node) {
        int folders = 0;
        int files = 0;
        if (node.getChildren() != null) {
            for (NodeTreeVO.NodeItemVO child : node.getChildren()) {
                if (child.getNodeType() == 0) folders++;
                else files++;
                int[] sub = countSubTree(child);
                folders += sub[0];
                files += sub[1];
            }
        }
        return new int[]{folders, files};
    }
    
    /**
     * 构建以指定文件夹为根的子树
     * @param folderId 目标文件夹ID
     * @param allNodes 所有相关节点列表
     * @return 包含该文件夹的单节点列表，或者空列表
     */
    public List<NodeTreeVO.NodeItemVO> buildFolderTree(Long folderId, List<Node> allNodes) {
        // 1. 创建Map
        Map<Long, NodeTreeVO.NodeItemVO> nodeMap = new HashMap<>();
        for (Node node : allNodes) {
            nodeMap.put(node.getNodeId(), nodeConvert.toNodeItemVO(node));
        }

        // 2. 建立父子关系
        for (Node node : allNodes) {
            NodeTreeVO.NodeItemVO currentNode = nodeMap.get(node.getNodeId());
            if (node.getParentId() != 0) {
                NodeTreeVO.NodeItemVO parentNode = nodeMap.get(node.getParentId());
                if (parentNode != null) {
                    parentNode.getChildren().add(currentNode);
                }
            }
        }

        // 3. 获取目标文件夹节点作为根节点
        NodeTreeVO.NodeItemVO rootNode = nodeMap.get(folderId);
        if (rootNode != null) {
            List<NodeTreeVO.NodeItemVO> result = new ArrayList<>();
            result.add(rootNode);
            return result;
        }
        
        return new ArrayList<>();
    }
}
