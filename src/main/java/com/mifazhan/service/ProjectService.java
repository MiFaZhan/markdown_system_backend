package com.mifazhan.service;

import java.util.List;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mifazhan.domain.dto.ProjectDTO;
import com.mifazhan.domain.dto.ProjectUpdateDTO;
import com.mifazhan.domain.entity.Project;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mifazhan.domain.vo.ProjectVO;
import jakarta.validation.Valid;

/**
* @author MIFAZHAN
* @description 针对表【projects】的数据库操作Service
* @createDate 2025-12-29 20:19:09
*/
public interface ProjectService extends IService<Project> {

    ProjectVO insertProject(ProjectDTO projectDTO);

    IPage<ProjectVO> pageProjects(Integer pageNum, Integer pageSize, String sortField, String sortOrder);

    List<ProjectVO> listProject(String keyword, String sortField, String sortOrder);

    ProjectVO getProject(Long projectId);

    ProjectVO updateProject(@Valid ProjectUpdateDTO projectUpdateDTO);

    void deleteProject(Long projectId);
}
