package com.mifazhan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mifazhan.domain.convert.NodeConvert;
import com.mifazhan.domain.dto.NodeDTO;
import com.mifazhan.domain.dto.NodeUpdateDTO;
import com.mifazhan.domain.entity.Node;
import com.mifazhan.domain.entity.MarkdownContent;
import com.mifazhan.domain.exception.BusinessException;
import com.mifazhan.domain.vo.NodeVO;
import com.mifazhan.domain.vo.NodeTreeVO;
import com.mifazhan.domain.vo.ProjectVO;
import com.mifazhan.service.NodeService;
import com.mifazhan.service.ProjectService;
import com.mifazhan.mapper.NodeMapper;
import com.mifazhan.mapper.MarkdownContentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author MIFAZHAN
* @description 针对表【markdown_file(Markdown 文件表)】的数据库操作Service实现
* @createDate 2025-12-16 15:01:03
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class NodeServiceImpl extends ServiceImpl<NodeMapper, Node>
    implements NodeService {

    private final NodeConvert nodeConvert;
    private final MarkdownContentMapper markdownContentMapper;
    private final ProjectService projectService;

    @Override
    public List<NodeVO> listNode() {
        List<Node> result = this.list();
        return nodeConvert.toVOList(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NodeVO insertNode(NodeDTO nodeDTO) {
        log.info("开始插入节点: {}", nodeDTO);

        Node node = nodeConvert.toEntity(nodeDTO);

        // 先插入 node 表，获得数据库自增的 nodeId
        this.save(node);
        log.info("成功插入节点，ID: {}", node.getNodeId());

        // 如果节点类型为文件(1)，则同步在 markdown_content 表中新增记录
        if (nodeDTO.getNodeType() == 1) {
            MarkdownContent markdownContent = new MarkdownContent();
            markdownContent.setNodeId(node.getNodeId());
            markdownContentMapper.insert(markdownContent);
            log.info("成功在 markdown_content 表中创建记录，节点ID: {}", node.getNodeId());
        }

        return nodeConvert.toVO(node);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NodeVO updateNode(NodeUpdateDTO nodeUpdateDTO) {
        log.info("开始修改节点, nodeUpdateDTO: {}", nodeUpdateDTO);
        Node node = nodeConvert.toEntity(nodeUpdateDTO);
        this.updateById(node);
        
        // 重新查询获取完整信息（包括projectId）
        Node updatedNode = this.getById(node.getNodeId());
        return nodeConvert.toVO(updatedNode);
    }

    @Override
    public NodeTreeVO getProjectTree(Long projectId) {
        log.info("开始构建项目节点树, projectId: {}", projectId);
        
        // 1. 查询项目信息
        ProjectVO project = projectService.getProject(projectId);
        NodeTreeVO result = new NodeTreeVO();
        result.setProjectId(projectId);
        result.setProjectName(project.getProjectName());
        
        // 2. 查询项目下所有节点，按创建时间升序排序
        LambdaQueryWrapper<Node> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Node::getProjectId, projectId)
                   .orderByAsc(Node::getCreationTime); // 按创建时间升序排序
        
        List<Node> allNodes = this.list(queryWrapper);
        
        if (allNodes.isEmpty()) {
            log.info("项目 {} 下没有节点", projectId);
            result.setTotalNodes(0);
            result.setFileCount(0);
            result.setFolderCount(0);
            return result;
        }
        
        // 3. 构建树形结构
        List<NodeTreeVO.NodeItemVO> rootNodes = buildNodeTree(allNodes);
        result.setRootNodes(rootNodes);
        
        // 4. 统计信息
        int fileCount = (int) allNodes.stream().filter(node -> node.getNodeType() == 1).count();
        int folderCount = (int) allNodes.stream().filter(node -> node.getNodeType() == 0).count();
        
        result.setTotalNodes(allNodes.size());
        result.setFileCount(fileCount);
        result.setFolderCount(folderCount);
        
        log.info("成功构建项目树，总节点数: {}, 文件数: {}, 文件夹数: {}", 
                allNodes.size(), fileCount, folderCount);
        
        return result;
    }

    /**
     * 使用Map映射方法构建树形结构
     * @param allNodes 所有节点列表（已按创建时间排序）
     * @return 树形结构的根节点列表
     */
    private List<NodeTreeVO.NodeItemVO> buildNodeTree(List<Node> allNodes) {
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
            
            if (node.getParentId() == null) {
                // 根节点
                rootNodes.add(currentNode);
            } else {
                // 子节点，添加到父节点的children中
                NodeTreeVO.NodeItemVO parentNode = nodeMap.get(node.getParentId());
                if (parentNode != null) {
                    parentNode.getChildren().add(currentNode);
                } else {
                    // 如果找不到父节点，可能是数据不一致，将其作为根节点处理
                    log.warn("节点 {} 的父节点 {} 不存在，将其作为根节点处理", 
                            node.getNodeId(), node.getParentId());
                    rootNodes.add(currentNode);
                }
            }
        }
        
        log.info("成功构建树形结构，根节点数量: {}", rootNodes.size());
        return rootNodes;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long deleteNode(Long nodeId) {
        log.info("开始删除节点, nodeId: {}", nodeId);
        Node node = this.getById(nodeId);
        
        if (node == null) {
            log.warn("节点不存在,ID: {}", nodeId);
            throw new BusinessException("节点不存在");
        }
        
        Long projectId = node.getProjectId(); // 保存projectId用于返回
        
        // 收集所有需要删除的节点ID(包括当前节点和所有子节点)
        List<Long> allNodeIds = new ArrayList<>();
        collectAllNodeIds(nodeId, allNodeIds);
        log.info("共收集到 {} 个节点需要删除: {}", allNodeIds.size(), allNodeIds);
        
        // 查询所有节点类型为1的节点，删除对应的markdown_content
        List<Node> allNodes = this.listByIds(allNodeIds);
        List<Long> fileNodeIds = allNodes.stream()
                .filter(n -> n.getNodeType() == 1)
                .map(Node::getNodeId)
                .collect(Collectors.toList());
        
        if (!fileNodeIds.isEmpty()) {
            int deletedContentCount = markdownContentMapper.deleteBatchIds(fileNodeIds);
            log.info("成功在 markdown_content 表中删除 {} 条记录,节点IDs: {}", deletedContentCount, fileNodeIds);
        }
        
        // 批量删除所有节点
        this.removeByIds(allNodeIds);
        log.info("成功删除 {} 个节点", allNodeIds.size());
        
        return projectId; // 返回projectId
    }
    
    /**
     * 递归收集当前节点及其所有子节点的ID
     * @param nodeId 当前节点ID
     * @param nodeIds 收集结果列表
     */
    private void collectAllNodeIds(Long nodeId, List<Long> nodeIds) {
        nodeIds.add(nodeId);
        
        // 查询所有子节点
        List<Node> children = this.lambdaQuery()
                .eq(Node::getParentId, nodeId)
                .list();
        
        // 递归收集子节点
        for (Node child : children) {
            collectAllNodeIds(child.getNodeId(), nodeIds);
        }
    }
}




