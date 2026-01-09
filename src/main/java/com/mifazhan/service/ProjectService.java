package com.mifazhan.service;

import com.mifazhan.domain.dto.ProjectDTO;
import com.mifazhan.domain.entity.Project;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mifazhan.domain.vo.ProjectVO;
import com.mifazhan.domain.vo.Result;

/**
* @author MIFAZHAN
* @description 针对表【projects】的数据库操作Service
* @createDate 2025-12-29 20:19:09
*/
public interface ProjectService extends IService<Project> {

    Result<ProjectVO> insertProject(ProjectDTO projectDTO);
}
