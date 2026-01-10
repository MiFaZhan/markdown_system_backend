package com.mifazhan.service.impl;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mifazhan.domain.convert.ProjectConvert;
import com.mifazhan.domain.dto.ProjectDTO;
import com.mifazhan.domain.dto.ProjectUpdateDTO;
import com.mifazhan.domain.entity.Project;
import com.mifazhan.domain.vo.ProjectVO;
import com.mifazhan.service.ProjectService;
import com.mifazhan.mapper.ProjectMapper;
import jakarta.validation.Valid;
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
    public Page<ProjectVO> pageProjects(Integer pageNum, Integer pageSize, String sortField, String sortOrder) {
        // 创建分页对象，默认值处理
        Page<Project> page = new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);

        // 添加排序条件
        if ("asc".equalsIgnoreCase(sortOrder)) {
            page.addOrder(OrderItem.asc(sortField));
        } else {
            page.addOrder(OrderItem.desc(sortField));
        }

        // 分页查询
        Page<Project> projectPage = this.page(page);

        // 转换为VO分页对象
        Page<ProjectVO> voPage = new Page<>(projectPage.getCurrent(), projectPage.getSize(), projectPage.getTotal());
        voPage.setRecords(projectConvert.toVOList(projectPage.getRecords()));

        return voPage;
    }

    @Override
    public ProjectVO getProject(Long projectId) {
        Project project = this.getById(projectId);
        return projectConvert.toVO(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectVO insertProject(ProjectDTO projectDTO) {
        log.info("开始插入Project: {}", projectDTO);

        Project project = projectConvert.toEntity(projectDTO);
        this.save(project);
        
        log.info("成功保存Project，ID: {}", project.getProjectId());
        return projectConvert.toVO(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectVO updateProject(@Valid ProjectUpdateDTO projectUpdateDTO) {
        log.info("开始修改Project: {}", projectUpdateDTO);
        Project project = projectConvert.toUpdateEntity(projectUpdateDTO);
        this.updateById(project);
        return projectConvert.toVO(project);
    }

    @Override
    public void deleteProject(Long projectId) {
        this.removeById(projectId);
    }
}


