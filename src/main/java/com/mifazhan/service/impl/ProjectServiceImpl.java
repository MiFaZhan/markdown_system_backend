package com.mifazhan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mifazhan.domain.convert.ProjectConvert;
import com.mifazhan.domain.dto.ProjectDTO;
import com.mifazhan.domain.dto.ProjectUpdateDTO;
import com.mifazhan.domain.entity.Project;
import com.mifazhan.domain.vo.ProjectVO;
import com.mifazhan.service.ProjectService;
import com.mifazhan.service.ShareLinkService;
import com.mifazhan.mapper.ProjectMapper;
import com.mifazhan.util.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mifazhan.mapper.NodeMapper;
import com.mifazhan.mapper.MarkdownContentMapper;
import com.mifazhan.domain.entity.Node;
import com.mifazhan.domain.entity.MarkdownContent;
import java.util.stream.Collectors;
import java.util.List;

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
    private final NodeMapper nodeMapper;
    private final MarkdownContentMapper markdownContentMapper;
    @Autowired
    @Lazy
    private ShareLinkService shareLinkService;

//    @Override
//    public IPage<ProjectVO> pageProjects(Integer pageNum, Integer pageSize, String sortField, String sortOrder) {
//        // 创建分页对象，默认值处理
//        Page<Project> page = new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
//
//        // 添加排序条件
//        if ("asc".equalsIgnoreCase(sortOrder)) {
//            page.setOrders(java.util.Collections.singletonList(OrderItem.asc(sortField)));
//        } else {
//            page.setOrders(java.util.Collections.singletonList(OrderItem.desc(sortField)));
//        }
//
//        // 分页查询
//        IPage<Project> projectPage = this.page(page);
//
//        // 转换为VO分页对象
//        IPage<ProjectVO> voPage = new Page<>(projectPage.getCurrent(), projectPage.getSize(), projectPage.getTotal());
//        voPage.setRecords(projectConvert.toVOList(projectPage.getRecords()));
//
//        return voPage;
//    }

    @Override
    public List<ProjectVO> listProject(String keyword, String sortField, String sortOrder) {
        Long currentUserId = UserContext.getCurrentUserId();
        
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Project::getUserId, currentUserId.intValue());
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper.like(Project::getProjectName, keyword.trim());
        }
        
        if ("asc".equalsIgnoreCase(sortOrder)) {
            queryWrapper.orderByAsc(Project::getCreationTime);
        } else {
            queryWrapper.orderByDesc(Project::getCreationTime);
        }
        List<Project> projectList = this.list(queryWrapper);
        return projectConvert.toVOList(projectList);
    }

    @Override
    public ProjectVO getProject(Long projectId) {
        Long currentUserId = UserContext.getCurrentUserId();
        
        Project project = this.getById(projectId);
        if (project == null || !project.getUserId().equals(currentUserId.intValue())) {
            throw new com.mifazhan.exception.BusinessException(403, "无权限访问该项目");
        }
        return projectConvert.toVO(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectVO insertProject(ProjectDTO projectDTO) {
        log.info("开始插入Project: {}", projectDTO);

        Project project = projectConvert.toEntity(projectDTO);
        project.setUserId(UserContext.getCurrentUserId().intValue());
        this.save(project);
        
        log.info("成功保存Project，ID: {}", project.getProjectId());
        return projectConvert.toVO(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectVO updateProject(@Valid ProjectUpdateDTO projectUpdateDTO) {
        log.info("开始修改Project: {}", projectUpdateDTO);
        
        Long currentUserId = UserContext.getCurrentUserId();
        
        Project existingProject = this.getById(projectUpdateDTO.getProjectId());
        if (existingProject == null || !existingProject.getUserId().equals(currentUserId.intValue())) {
            throw new com.mifazhan.exception.BusinessException(403, "无权限修改该项目");
        }
        
        Project project = projectConvert.toUpdateEntity(projectUpdateDTO);
        this.updateById(project);
        return projectConvert.toVO(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProject(Long projectId) {
        Long currentUserId = UserContext.getCurrentUserId();
        
        Project project = this.getById(projectId);
        if (project == null || !project.getUserId().equals(currentUserId.intValue())) {
            throw new com.mifazhan.exception.BusinessException(403, "无权限删除该项目");
        }

        // 1. 查询项目下所有节点
        LambdaQueryWrapper<Node> nodeQueryWrapper = new LambdaQueryWrapper<>();
        nodeQueryWrapper.eq(Node::getProjectId, projectId);
        List<Node> nodes = nodeMapper.selectList(nodeQueryWrapper);

        if (!nodes.isEmpty()) {
            List<Long> nodeIds = nodes.stream().map(Node::getNodeId).collect(Collectors.toList());

            // 2. 删除节点对应的内容
            LambdaQueryWrapper<MarkdownContent> contentQueryWrapper = new LambdaQueryWrapper<>();
            contentQueryWrapper.in(MarkdownContent::getNodeId, nodeIds);
            markdownContentMapper.delete(contentQueryWrapper);

            // 3. 删除所有节点
            nodeMapper.delete(nodeQueryWrapper);
            
            log.info("级联删除了 {} 个节点及其内容", nodes.size());

            // 逻辑删除所有节点的分享链接
            shareLinkService.deleteNodeShares(nodeIds);
        }

        // 逻辑删除项目本身的分享链接 (targetType=2 为项目)
        shareLinkService.deleteShareByTarget(2, projectId);
        
        // 4. 删除项目
        this.removeById(projectId);
        log.info("成功删除项目: {}", projectId);
    }

    @Override
    public List<ProjectVO> listRecycleBinProjects(String keyword, String sortField, String sortOrder) {
        Long currentUserId = UserContext.getCurrentUserId();
        
        // 验证排序字段，防止SQL注入
        String safeSortField = "creation_time"; // 默认
        if ("update_time".equalsIgnoreCase(sortField)) {
            safeSortField = "update_time";
        } else if ("project_name".equalsIgnoreCase(sortField)) {
            safeSortField = "project_name";
        }
        
        // 验证排序顺序
        String safeSortOrder = "asc".equalsIgnoreCase(sortOrder) ? "ASC" : "DESC";

        List<Project> projects = baseMapper.selectDeletedProjects(currentUserId.intValue(), keyword, safeSortField, safeSortOrder);
        return projectConvert.toVOList(projects);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restoreProject(Long projectId) {
        Long currentUserId = UserContext.getCurrentUserId();
        
        // Use custom select to include deleted projects
        Project project = baseMapper.selectProjectIncludingDeleted(projectId);
        
        if (project == null || !project.getUserId().equals(currentUserId.intValue())) {
            throw new com.mifazhan.exception.BusinessException(403, "无权限恢复该项目");
        }

        // 检查当前用户下是否存在同名未删除项目，如有则按 "名称 (1)" 规则重命名
        String originalName = project.getProjectName();
        String uniqueName = getUniqueProjectName(project.getUserId(), originalName);
        if (!uniqueName.equals(originalName)) {
            log.info("恢复项目时发生命名冲突，重命名: {} -> {}", originalName, uniqueName);
            baseMapper.updateProjectNameIgnoringDeleted(projectId, uniqueName);
        }
        
        // Restore project
        baseMapper.restoreProject(projectId);
        
        // Restore nodes
        nodeMapper.restoreNodesByProjectId(projectId);
        
        // Restore content
        List<Node> nodes = nodeMapper.selectAllNodesByProjectId(projectId);
        if (!nodes.isEmpty()) {
            List<Long> nodeIds = nodes.stream()
                .filter(n -> n.getNodeType() == 1) // Only files have content
                .map(Node::getNodeId)
                .collect(Collectors.toList());
            if (!nodeIds.isEmpty()) {
                markdownContentMapper.restoreContentByNodeIds(nodeIds);
            }

            // Restore share links for all nodes
            List<Long> allNodeIds = nodes.stream().map(Node::getNodeId).collect(Collectors.toList());
            shareLinkService.restoreNodeShares(allNodeIds);
        }

        // Restore project share link
        shareLinkService.restoreShareByTarget(2, projectId);
        
        log.info("成功恢复项目: {}", projectId);
    }

    /**
     * 获取当前用户下唯一的项目名称（仅考虑未删除项目）
     * @param userId 用户ID
     * @param originalName 原始项目名称
     * @return 不与现有未删除项目冲突的名称
     */
    private String getUniqueProjectName(Integer userId, String originalName) {
        String baseName = originalName;
        String newName = baseName;
        int counter = 1;

        while (true) {
            long count = this.lambdaQuery()
                    .eq(Project::getUserId, userId)
                    .eq(Project::getProjectName, newName)
                    .count();
            if (count == 0) {
                return newName;
            }

            newName = baseName + " (" + counter + ")";
            counter++;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void physicalDeleteProject(Long projectId) {
        Long currentUserId = UserContext.getCurrentUserId();
        
        Project project = baseMapper.selectProjectIncludingDeleted(projectId);
        if (project == null || !project.getUserId().equals(currentUserId.intValue())) {
            throw new com.mifazhan.exception.BusinessException(403, "无权限删除该项目");
        }
        
        // Delete content first
        List<Node> nodes = nodeMapper.selectAllNodesByProjectId(projectId);
        if (!nodes.isEmpty()) {
             List<Long> nodeIds = nodes.stream()
                .filter(n -> n.getNodeType() == 1)
                .map(Node::getNodeId)
                .collect(Collectors.toList());
            if (!nodeIds.isEmpty()) {
                markdownContentMapper.physicalDeleteContentByNodeIds(nodeIds);
            }

            // Delete share links for nodes
            List<Long> allNodeIds = nodes.stream().map(Node::getNodeId).collect(Collectors.toList());
            shareLinkService.physicalDeleteNodeShares(allNodeIds);
        }
        
        // Delete nodes
        nodeMapper.physicalDeleteNodesByProjectId(projectId);

        // Delete project share link
        shareLinkService.physicalDeleteShareByTarget(2, projectId);
        
        // Delete project
        baseMapper.physicalDeleteProject(projectId);
        
        log.info("成功物理删除项目: {}", projectId);
    }
}

