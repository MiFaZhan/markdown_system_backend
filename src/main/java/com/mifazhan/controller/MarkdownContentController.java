package com.mifazhan.controller;

import com.mifazhan.annotation.RequirePermission;
import com.mifazhan.domain.dto.MarkdownContentDTO;
import com.mifazhan.domain.vo.MarkdownContentVO;
import com.mifazhan.domain.vo.Result;
import com.mifazhan.service.MarkdownContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

/**
 * @author MIFAZHAN
 */
@RestController
@RequestMapping("/api/markdown-content")
@Slf4j
public class MarkdownContentController {
    @Autowired
    private MarkdownContentService markdownContentService;

    @GetMapping("/{nodeId}")
    @RequirePermission("content:read")
    public Result<MarkdownContentVO> getMarkdownContent(@PathVariable Long nodeId) {
        return Result.success(markdownContentService.getMarkdownContentByNodeId(nodeId));
    }

    @PutMapping("/{nodeId}")
    @RequirePermission("content:*")
    public Result<MarkdownContentVO> updateMarkdownContent(
            @PathVariable Long nodeId,
            @Valid @RequestBody MarkdownContentDTO markdownContentDTO) {
        markdownContentDTO.setNodeId(nodeId);
        return Result.success(markdownContentService.updateMarkdownContent(nodeId, markdownContentDTO));
    }

}
