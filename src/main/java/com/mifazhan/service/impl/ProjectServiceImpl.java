package com.mifazhan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mifazhan.domain.convert.ProjectConvert;
import com.mifazhan.domain.dto.ProjectDTO;
import com.mifazhan.domain.entity.Project;
import com.mifazhan.domain.exception.BusinessException;
import com.mifazhan.domain.vo.ProjectVO;
import com.mifazhan.domain.vo.Result;
import com.mifazhan.service.ProjectService;
import com.mifazhan.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author MIFAZHAN
* @description 针对表【project】的数据库操作Service实现
* @createDate 2025-12-29 20:19:09
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project>
    implements ProjectService {

    private final ProjectConvert projectConvert;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<ProjectVO> insertProject(ProjectDTO projectDTO) {
        log.info("开始插入Project: {}", projectDTO);
        
        if (projectDTO == null){
            log.error("新增projectDTO为空");
            throw new BusinessException(400, "新增projectDTO不能为空");
        }
        
        // 验证projectName不为空
        if (projectDTO.getProjectName() == null || projectDTO.getProjectName().trim().isEmpty()) {
            log.error("项目名称不能为空");
            throw new BusinessException(400, "项目名称不能为空");
        }

        Project project = projectConvert.toEntity(projectDTO);

        if(this.save(project)){
            log.info("成功保存Project，ID: {}", project.getProjectId());
            ProjectVO result = projectConvert.toVO(this.getById(project.getProjectId()));
            log.info("返回的VO对象: {}", result);
            return Result.success(result);
        }else {
            log.error("新增Project失败，DTO: {}", projectDTO);
            throw new BusinessException(500, "新增Project失败");
        }
    }
}


