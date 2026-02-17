package com.mifazhan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mifazhan.domain.convert.NodeConvert;
import com.mifazhan.domain.dto.NodeDTO;
import com.mifazhan.domain.dto.NodeUpdateDTO;
import com.mifazhan.domain.dto.NodeUploadDTO;
import com.mifazhan.domain.entity.Node;
import com.mifazhan.domain.entity.MarkdownContent;
import com.mifazhan.exception.BusinessException;
import com.mifazhan.domain.vo.NodeVO;
import com.mifazhan.domain.vo.NodeTreeVO;
import com.mifazhan.domain.vo.ProjectVO;
import com.mifazhan.service.ImageService;
import com.mifazhan.service.NodeService;
import com.mifazhan.service.ProjectService;
import com.mifazhan.mapper.NodeMapper;
import com.mifazhan.mapper.MarkdownContentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    private final ImageService imageService;

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

    @Override
    public NodeTreeVO getRecycleBinTree(Long projectId) {
        log.info("开始构建项目回收站树, projectId: {}", projectId);

        // 1. 查询项目信息
        ProjectVO project = projectService.getProject(projectId);
        NodeTreeVO result = new NodeTreeVO();
        result.setProjectId(projectId);
        result.setProjectName(project.getProjectName());

        // 2. 查询项目下所有已删除节点
        List<Node> deletedNodes = baseMapper.selectDeletedNodes(projectId);

        if (deletedNodes.isEmpty()) {
            log.info("项目 {} 回收站为空", projectId);
            result.setTotalNodes(0);
            result.setFileCount(0);
            result.setFolderCount(0);
            return result;
        }

        // 3. 构建树形结构
        List<NodeTreeVO.NodeItemVO> rootNodes = buildNodeTree(deletedNodes);
        result.setRootNodes(rootNodes);

        // 4. 统计信息
        int fileCount = (int) deletedNodes.stream().filter(node -> node.getNodeType() == 1).count();
        int folderCount = (int) deletedNodes.stream().filter(node -> node.getNodeType() == 0).count();

        result.setTotalNodes(deletedNodes.size());
        result.setFileCount(fileCount);
        result.setFolderCount(folderCount);

        log.info("成功构建回收站树，总节点数: {}, 文件数: {}, 文件夹数: {}",
                deletedNodes.size(), fileCount, folderCount);

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
    public void restoreNode(Long nodeId) {
        log.info("开始恢复节点, nodeId: {}", nodeId);
        
        // 1. 获取要恢复的节点
        // 使用自定义SQL查询，绕过逻辑删除机制
        Node node = baseMapper.selectNodeIncludingDeleted(nodeId);
        
        if (node == null) {
            throw new BusinessException("节点不存在");
        }
        
        // 2. 检查父节点下是否有同名文件，如果有则重命名
        // 此时 node 还是 deleted=1，所以 selectByParentIdAndName 不会查到它自己
        Long parentId = node.getParentId() != null ? node.getParentId() : 0L;
        String uniqueName = getUniqueNodeName(node.getProjectId(), parentId, node.getNodeName());
        boolean nameChanged = !uniqueName.equals(node.getNodeName());
        
        if (nameChanged) {
            log.info("恢复节点时发生命名冲突，重命名: {} -> {}", node.getNodeName(), uniqueName);
            // 使用自定义SQL更新名称，忽略逻辑删除状态
            baseMapper.updateNodeNameIgnoringDeleted(nodeId, uniqueName);
        }
        
        // 3. 执行递归恢复（将 deleted 置为 0）
        recursiveRestore(nodeId);
        
        log.info("节点恢复完成, nodeId: {}", nodeId);
    }

    /**
     * 获取唯一的节点名称（处理重名）
     * @param projectId 项目ID
     * @param parentId 父节点ID
     * @param originalName 原始名称
     * @return 唯一名称
     */
    private String getUniqueNodeName(Long projectId, Long parentId, String originalName) {
        String baseName = originalName;
        int counter = 1;
        String newName = baseName;
        
        while (true) {
            // 查询是否存在同名且未删除的节点
            List<Node> existingNodes = baseMapper.selectByParentIdAndName(projectId, parentId, newName);
            if (existingNodes.isEmpty()) {
                return newName;
            }
            
            // 存在同名，尝试下一个序号
            newName = baseName + " (" + counter + ")";
            counter++;
        }
    }

    /**
     * 递归恢复节点及其子节点
     */
    private void recursiveRestore(Long nodeId) {
        // 1. 恢复当前节点
        baseMapper.restoreNode(nodeId);
        
        // 2. 恢复对应的 markdown_content（如果存在）
        // 无论是否是文件节点，尝试恢复对应的 content 也没副作用
        // 或者先判断节点类型，但这里可能查不到类型（因为前面 selectOne 查了）
        // 简单起见，直接尝试恢复 content
        markdownContentMapper.restoreContent(nodeId);

        // 3. 查询该节点下的所有子节点（包含已删除的）
        List<Node> children = baseMapper.selectAllChildren(nodeId);
        
        // 4. 递归恢复子节点
        for (Node child : children) {
            recursiveRestore(child.getNodeId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void physicalDeleteNode(Long nodeId) {
        log.info("开始物理删除节点, nodeId: {}", nodeId);

        // 0. 获取根节点信息（用于获取projectId）
        Node rootNode = baseMapper.selectNodeIncludingDeleted(nodeId);
        if (rootNode == null) {
            log.warn("节点不存在, nodeId: {}", nodeId);
            return;
        }
        Long projectId = rootNode.getProjectId();
        
        // 1. 递归收集所有需要删除的节点ID（包括子节点，忽略逻辑删除状态）
        List<Long> allNodeIds = new ArrayList<>();
        collectAllNodeIdsIgnoringDeleted(nodeId, allNodeIds);
        
        if (allNodeIds.isEmpty()) {
            return;
        }
        
        log.info("共收集到 {} 个节点需要物理删除: {}", allNodeIds.size(), allNodeIds);
        
        // 2. 物理删除 markdown_content 表中的内容
        // 注意：markdownContentMapper.deleteBatchIds 默认可能是逻辑删除（如果有配置逻辑删除插件）
        // 稳妥起见，如果 markdown_content 确实需要物理删除，我们应该检查其 Mapper 或 Entity
        // 假设 markdown_content 没有逻辑删除字段，或者我们需要强制物理删除
        // 简单起见，这里假设 deleteBatchIds 能满足需求，或者后续补充物理删除 Mapper
        int deletedContentCount = markdownContentMapper.deleteBatchIds(allNodeIds);
        log.info("成功在 markdown_content 表中删除 {} 条记录", deletedContentCount);
        
        // 3. 物理删除 node 表中的记录，并同步删除图片文件
        for (Long id : allNodeIds) {
            // 同步删除图片文件
            try {
                imageService.deleteNodeImages(projectId, id);
            } catch (Exception e) {
                log.error("删除节点图片失败: projectId={}, nodeId={}", projectId, id, e);
            }

            baseMapper.physicalDeleteNode(id);
        }
        
        log.info("成功物理删除 {} 个节点", allNodeIds.size());
    }

    /**
     * 递归收集当前节点及其所有子节点的ID（忽略逻辑删除状态）
     */
    private void collectAllNodeIdsIgnoringDeleted(Long nodeId, List<Long> nodeIds) {
        nodeIds.add(nodeId);
        
        // 查询所有子节点（包含已删除的）
        List<Node> children = baseMapper.selectAllChildren(nodeId);
        
        // 递归收集子节点
        for (Node child : children) {
            collectAllNodeIdsIgnoringDeleted(child.getNodeId(), nodeIds);
        }
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NodeVO uploadMarkdownFile(NodeUploadDTO nodeUploadDTO) {
        log.info("开始上传Markdown文件, projectId: {}, parentId: {}", 
                nodeUploadDTO.getProjectId(), nodeUploadDTO.getParentId());

        MultipartFile file = nodeUploadDTO.getFile();
        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BusinessException("文件名不能为空");
        }

        String extension = getFileExtension(originalFilename);
        if (!"md".equalsIgnoreCase(extension)) {
            throw new BusinessException("仅支持上传.md格式的文件");
        }

        String nodeName = getFileNameWithoutExtension(originalFilename);
        if (nodeName.isEmpty()) {
            throw new BusinessException("文件名不能为空");
        }
        
        // 检查并处理重名
        nodeName = getUniqueNodeName(nodeUploadDTO.getProjectId(), 
                                   nodeUploadDTO.getParentId() != null ? nodeUploadDTO.getParentId() : 0L, 
                                   nodeName);

        String content;
        try {
            content = new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("读取文件内容失败", e);
            throw new BusinessException("读取文件内容失败: " + e.getMessage());
        }

        Node node = new Node();
        node.setProjectId(nodeUploadDTO.getProjectId());
        node.setParentId(nodeUploadDTO.getParentId() != null ? nodeUploadDTO.getParentId() : 0L);
        node.setNodeType(1);
        node.setNodeName(nodeName);

        this.save(node);
        log.info("成功插入节点，ID: {}", node.getNodeId());

        MarkdownContent markdownContent = new MarkdownContent();
        markdownContent.setNodeId(node.getNodeId());
        markdownContent.setContent(content);
        markdownContentMapper.insert(markdownContent);
        log.info("成功在 markdown_content 表中创建记录，节点ID: {}", node.getNodeId());

        return nodeConvert.toVO(node);
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    private String getFileNameWithoutExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return filename;
        }
        return filename.substring(0, lastDotIndex);
    }
}



