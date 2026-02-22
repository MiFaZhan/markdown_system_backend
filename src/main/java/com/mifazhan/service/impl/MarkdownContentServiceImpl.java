package com.mifazhan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mifazhan.domain.convert.MarkdownContentConvert;
import com.mifazhan.domain.dto.MarkdownContentDTO;
import com.mifazhan.domain.entity.MarkdownContent;
import com.mifazhan.domain.entity.Node;
import com.mifazhan.domain.entity.Project;
import com.mifazhan.domain.vo.MarkdownContentVO;
import com.mifazhan.exception.BusinessException;
import com.mifazhan.mapper.MarkdownContentMapper;
import com.mifazhan.mapper.NodeMapper;
import com.mifazhan.mapper.ProjectMapper;
import com.mifazhan.service.MarkdownContentService;
import com.mifazhan.util.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarkdownContentServiceImpl extends ServiceImpl<MarkdownContentMapper, MarkdownContent> implements MarkdownContentService {

    private final MarkdownContentConvert markdownContentConvert;
    private final MarkdownContentMapper markdownContentMapper;
    private final NodeMapper nodeMapper;
    private final ProjectMapper projectMapper;

    @Override
    public MarkdownContentVO getMarkdownContentByNodeId(Long nodeId) {
        Node node = nodeMapper.selectById(nodeId);
        if (node == null) {
            throw new BusinessException("节点不存在");
        }
        if (node.getNodeType() != 1) {
            throw new BusinessException("只能获取文件类型节点的内容");
        }

        Long currentUserId = UserContext.getCurrentUserId();
        Project project = projectMapper.selectById(node.getProjectId());
        if (project == null || !project.getUserId().equals(currentUserId.intValue())) {
            log.warn("用户 {} 尝试访问无权限的项目的内容", currentUserId);
            throw new BusinessException(403, "无权限访问该项目");
        }

        MarkdownContent markdownContent = markdownContentMapper.selectById(nodeId);
        return markdownContentConvert.toVO(markdownContent);
    }

    @Override
    public MarkdownContentVO updateMarkdownContent(Long nodeId, @Valid MarkdownContentDTO markdownContentDTO) {
        Node node = nodeMapper.selectById(nodeId);
        if (node == null) {
            throw new BusinessException("节点不存在");
        }
        if (node.getNodeType() != 1) {
            throw new BusinessException("只能更新文件类型节点的内容");
        }

        Long currentUserId = UserContext.getCurrentUserId();
        Project project = projectMapper.selectById(node.getProjectId());
        if (project == null || !project.getUserId().equals(currentUserId.intValue())) {
            log.warn("用户 {} 尝试更新无权限的项目的内容", currentUserId);
            throw new BusinessException(403, "无权限访问该项目");
        }

        MarkdownContent existingContent = markdownContentMapper.selectById(nodeId);
        if (existingContent == null) {
            throw new BusinessException("内容记录不存在");
        }
        
        log.info("更新内容 - nodeId: {}, 前端version: {}, 数据库version: {}", 
                nodeId, markdownContentDTO.getVersion(), existingContent.getVersion());
        
        // 检查版本号
        if (markdownContentDTO.getVersion() != null && 
            !markdownContentDTO.getVersion().equals(existingContent.getVersion())) {
            log.warn("版本冲突 - 前端version: {}, 数据库version: {}", 
                    markdownContentDTO.getVersion(), existingContent.getVersion());
            throw new BusinessException("内容已被其他用户修改，请刷新后重试");
        }
        
        // 更新内容
        existingContent.setContent(markdownContentDTO.getContent());
        // version 字段由 MyBatis-Plus 的乐观锁插件自动处理
        
        int updateCount = markdownContentMapper.updateById(existingContent);
        log.info("更新结果 - 影响行数: {}, 新version: {}", updateCount, existingContent.getVersion());
        
        if (updateCount == 0) {
            throw new BusinessException("更新失败，内容可能已被修改");
        }
        
        // 重新查询以获取最新的数据（包括更新后的 version）
        MarkdownContent updatedContent = markdownContentMapper.selectById(nodeId);
        log.info("返回更新后的数据 - version: {}", updatedContent.getVersion());
        
        return markdownContentConvert.toVO(updatedContent);
    }
}
