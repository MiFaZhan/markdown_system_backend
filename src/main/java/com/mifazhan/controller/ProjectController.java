package com.mifazhan.controller;


import java.util.List;

import com.mifazhan.annotation.RequirePermission;
import com.mifazhan.domain.dto.ProjectDTO;
import com.mifazhan.domain.dto.ProjectUpdateDTO;
import com.mifazhan.domain.vo.ProjectVO;
import com.mifazhan.domain.vo.Result;
import com.mifazhan.service.ProjectService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author MIFAZHAN
 * @description 针对表【project】的数据库操作Controller
 * @createDate 2025-12-29 20:19:09
 */
@RestController
@RequestMapping("/api/project")
@Slf4j
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    /**
     * 分页查询项目列表
     */
    // @GetMapping
    // public Result<Page<ProjectVO>> listProjects(
    //         @RequestParam(defaultValue = "1") Integer pageNum,
    //         @RequestParam(defaultValue = "10") Integer pageSize,
    //         @RequestParam(defaultValue = "creation_time") String sortField,
    //         @RequestParam(defaultValue = "asc") String sortOrder) {
    //     log.info("分页查询项目列表, pageNum: {}, pageSize: {}, sortField: {}, sortOrder: {}", 
    //             pageNum, pageSize, sortField, sortOrder);
    //     return Result.success(projectService.pageProjects(pageNum, pageSize, sortField, sortOrder));
    // }

    /**
     * 查询所有项目列表
     */
    @GetMapping
    @RequirePermission("project:read")
    public Result<List<ProjectVO>> z(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "creation_time") String sortField,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        log.info("查询所有项目列表, keyword: {}, sortField: {}, sortOrder: {}", keyword, sortField, sortOrder);
        return Result.success(projectService.listProject(keyword, sortField, sortOrder));
    }

    /**
     * 根据ID查询项目
     */
    @GetMapping("/{projectId}")
    @RequirePermission("project:read")
    public Result<ProjectVO> getProject(@PathVariable Long projectId) {
        log.info("查询项目, projectId: {}", projectId);
        return Result.success(projectService.getProject(projectId));
    }

    /**
     * 创建项目
     */
    @PostMapping
    @RequirePermission("project:*")
    public Result<ProjectVO> insertProject(@Valid @RequestBody ProjectDTO projectDTO) {
        log.info("创建项目: {}", projectDTO);
        return Result.success(projectService.insertProject(projectDTO));
    }

    /**
     * 修改项目
     */
    @PutMapping
    @RequirePermission("project:*")
    public Result<ProjectVO> updateProject(@Valid @RequestBody ProjectUpdateDTO projectUpdateDTO) {
        log.info("修改项目: {}", projectUpdateDTO);
        return Result.success(projectService.updateProject(projectUpdateDTO));
    }

    /**
     * 删除项目
     */
    @DeleteMapping
    @RequirePermission("project:*")
    public Result<Void> deleteProject(@RequestParam Long projectId) {
        log.info("删除项目, projectId: {}", projectId);
        projectService.deleteProject(projectId);
        return Result.success();
    }

}
