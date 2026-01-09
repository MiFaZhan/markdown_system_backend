package com.mifazhan.controller;


import com.mifazhan.domain.dto.ProjectDTO;
import com.mifazhan.domain.vo.ProjectVO;
import com.mifazhan.domain.vo.Result;
import com.mifazhan.service.ProjectService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author MIFAZHAN
 * @description 针对表【project】的数据库操作Controller
 * @createDate 2025-12-29 20:19:09
 */
@RestController
@RequestMapping("/project")
@Slf4j
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @PostMapping
    public Result<ProjectVO> insertProject(@Valid @RequestBody ProjectDTO projectDTO){
        return projectService.insertProject(projectDTO);
    }
}
