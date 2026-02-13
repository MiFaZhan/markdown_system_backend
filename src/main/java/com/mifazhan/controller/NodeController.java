package com.mifazhan.controller;

import com.mifazhan.domain.dto.NodeDTO;
import com.mifazhan.domain.dto.NodeUpdateDTO;
import com.mifazhan.domain.dto.NodeUploadDTO;
import com.mifazhan.domain.vo.NodeVO;
import com.mifazhan.domain.vo.NodeTreeVO;
import com.mifazhan.domain.vo.Result;
import com.mifazhan.service.NodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

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
    public Result<NodeVO> insertNode(@Valid @RequestBody NodeDTO nodeDTO) {
        return Result.success(nodeService.insertNode(nodeDTO));
    }

    @PostMapping("/upload")
    public Result<NodeVO> uploadMarkdownFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("projectId") Long projectId,
            @RequestParam(value = "parentId", required = false) Long parentId) {
        NodeUploadDTO nodeUploadDTO = new NodeUploadDTO();
        nodeUploadDTO.setFile(file);
        nodeUploadDTO.setProjectId(projectId);
        nodeUploadDTO.setParentId(parentId);
        return Result.success(nodeService.uploadMarkdownFile(nodeUploadDTO));
    }

    @PutMapping
    public Result<NodeVO> updateNode(@Valid @RequestBody NodeUpdateDTO nodeUpdateDTO) {
        return Result.success(nodeService.updateNode(nodeUpdateDTO));
    }

    @DeleteMapping("/{nodeId}")
    public Result<Void> deleteNode(@PathVariable Long nodeId) {
        nodeService.deleteNode(nodeId);
        return Result.success();
    }
}