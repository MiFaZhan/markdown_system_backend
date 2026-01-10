package com.mifazhan.controller;

import com.mifazhan.domain.dto.NodeDTO;
import com.mifazhan.domain.dto.NodeUpdateDTO;
import com.mifazhan.domain.vo.NodeVO;
import com.mifazhan.domain.vo.NodeTreeVO;
import com.mifazhan.domain.vo.Result;
import com.mifazhan.service.NodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

/**
 * @author MIFAZHAN
 */
@RestController
@RequestMapping("/api/node")
@Slf4j
public class NodeController {
    @Autowired
    private NodeService nodeService;

//    @GetMapping
//    public Result<List<NodeVO>> listNode() {
//        return Result.success(nodeService.listNode());
//    }

    /**
     * 获取项目的节点树形结构
     * @param projectId 项目ID
     * @return 包含项目信息和节点树的完整响应
     */
    @GetMapping("/tree/{projectId}")
    public Result<NodeTreeVO> getProjectTree(@PathVariable Long projectId) {
        return Result.success(nodeService.getProjectTree(projectId));
    }

    @PostMapping
    public Result<NodeTreeVO> insertNode(@Valid @RequestBody NodeDTO nodeDTO) {
        nodeService.insertNode(nodeDTO);
        // 插入后返回完整的项目树形结构，确保前端数据一致性
        return Result.success(nodeService.getProjectTree(nodeDTO.getProjectId()));
    }

    @PutMapping
    public Result<NodeTreeVO> updateNode(@Valid @RequestBody NodeUpdateDTO nodeUpdateDTO) {
        NodeVO updatedNode = nodeService.updateNode(nodeUpdateDTO);
        // 更新后返回完整的项目树形结构，确保前端数据一致性
        return Result.success(nodeService.getProjectTree(updatedNode.getProjectId()));
    }

    @DeleteMapping
    public Result<NodeTreeVO> deleteNode(@RequestBody Long nodeId) {
        Long projectId = nodeService.deleteNode(nodeId);
        // 删除后返回完整的项目树形结构，确保前端数据一致性
        return Result.success(nodeService.getProjectTree(projectId));
    }
}