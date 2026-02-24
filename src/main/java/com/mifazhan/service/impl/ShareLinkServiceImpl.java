package com.mifazhan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mifazhan.domain.convert.ShareLinkConvert;
import com.mifazhan.domain.dto.ShareAccessDTO;
import com.mifazhan.domain.dto.ShareCreateDTO;
import com.mifazhan.domain.dto.ShareUpdateDTO;
import com.mifazhan.domain.entity.Node;
import com.mifazhan.domain.entity.Project;
import com.mifazhan.domain.entity.ShareLink;
import com.mifazhan.domain.vo.MarkdownContentVO;
import com.mifazhan.domain.vo.NodeTreeVO;
import com.mifazhan.domain.vo.ShareContentVO;
import com.mifazhan.domain.vo.ShareLinkVO;
import com.mifazhan.exception.BusinessException;
import com.mifazhan.mapper.NodeMapper;
import com.mifazhan.mapper.ProjectMapper;
import com.mifazhan.mapper.ShareLinkMapper;
import com.mifazhan.domain.entity.MarkdownContent;
import com.mifazhan.domain.convert.MarkdownContentConvert;
import com.mifazhan.mapper.MarkdownContentMapper;
import com.mifazhan.service.MarkdownContentService;
import com.mifazhan.service.ShareLinkService;
import com.mifazhan.service.helper.NodeTreeHelper;
import com.mifazhan.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShareLinkServiceImpl extends ServiceImpl<ShareLinkMapper, ShareLink> implements ShareLinkService {

    private final ShareLinkConvert shareLinkConvert;
    private final NodeMapper nodeMapper;
    private final ProjectMapper projectMapper;
    private final NodeTreeHelper nodeTreeHelper;
    private final MarkdownContentService markdownContentService;
    private final MarkdownContentMapper markdownContentMapper;
    private final MarkdownContentConvert markdownContentConvert;

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShareLinkVO createShare(ShareCreateDTO shareCreateDTO) {
        Long userId = UserContext.getCurrentUserId();
        
        Integer targetType = shareCreateDTO.getTargetType();
        Long targetId = shareCreateDTO.getTargetId();
        
        if (targetType == 2) {
            Project project = projectMapper.selectById(targetId);
            if (project == null) {
                throw new BusinessException("分享的项目不存在");
            }
            if (!project.getUserId().equals(userId.intValue())) {
                throw new BusinessException("无权分享该项目");
            }
        } else {
            Node node = nodeMapper.selectById(targetId);
            if (node == null) {
                throw new BusinessException("分享的节点不存在");
            }
            Project project = projectMapper.selectById(node.getProjectId());
            if (project == null) {
                throw new BusinessException("节点所属项目不存在");
            }
            if (!project.getUserId().equals(userId.intValue())) {
                throw new BusinessException("无权分享该节点");
            }
        }

        String targetName = getTargetName(targetType, targetId);

        String shareCode = generateUniqueShareCode();

        ShareLink shareLink = new ShareLink();
        shareLink.setTargetType(shareCreateDTO.getTargetType());
        shareLink.setUserId(userId);
        shareLink.setShareCode(shareCode);
        shareLink.setPassword(shareCreateDTO.getPassword());
        shareLink.setExpireTime(shareCreateDTO.getExpireTime());

        if (shareCreateDTO.getTargetType() == 2) {
            shareLink.setProjectId(shareCreateDTO.getTargetId());
            shareLink.setNodeId(null);
        } else {
            shareLink.setNodeId(shareCreateDTO.getTargetId());
            Long projectId = getProjectId(shareCreateDTO.getTargetType(), shareCreateDTO.getTargetId());
            shareLink.setProjectId(projectId);
        }

        this.save(shareLink);

        ShareLinkVO shareLinkVO = shareLinkConvert.toVO(shareLink);
        shareLinkVO.setTargetName(targetName);
        return shareLinkVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShareLinkVO updateShare(ShareUpdateDTO shareUpdateDTO) {
        Long userId = UserContext.getCurrentUserId();
        ShareLink shareLink = this.getById(shareUpdateDTO.getShareId());

        if (shareLink == null) {
            throw new BusinessException("分享链接不存在");
        }

        if (!shareLink.getUserId().equals(userId)) {
            throw new BusinessException("无权修改此分享链接");
        }

        if (shareUpdateDTO.getPassword() != null) {
            shareLink.setPassword(shareUpdateDTO.getPassword());
        }
        
        if (Boolean.TRUE.equals(shareUpdateDTO.getClearExpireTime())) {
            shareLink.setExpireTime(null);
        } else if (shareUpdateDTO.getExpireTime() != null) {
            shareLink.setExpireTime(shareUpdateDTO.getExpireTime());
        }

        this.updateById(shareLink);

        ShareLinkVO shareLinkVO = shareLinkConvert.toVO(shareLink);
        shareLinkVO.setTargetName(getTargetName(shareLink.getTargetType(), getTargetId(shareLink)));
        return shareLinkVO;
    }

    @Override
    public ShareLinkVO accessShare(String shareCode, ShareAccessDTO shareAccessDTO) {
        ShareLink shareLink = baseMapper.selectByShareCode(shareCode);
        
        if (shareLink == null || shareLink.getDeleted() == 1) {
            throw new BusinessException("分享链接不存在或已失效");
        }

        if (shareLink.getExpireTime() != null && shareLink.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("分享链接已过期");
        }

        if (shareLink.getPassword() != null && !shareLink.getPassword().isEmpty()) {
            if (shareAccessDTO == null || shareAccessDTO.getPassword() == null || !shareAccessDTO.getPassword().equals(shareLink.getPassword())) {
                throw new BusinessException("访问密码错误");
            }
        }

        ShareLinkVO shareLinkVO = shareLinkConvert.toVO(shareLink);
        shareLinkVO.setTargetName(getTargetName(shareLink.getTargetType(), getTargetId(shareLink)));
        return shareLinkVO;
    }

    @Override
    public ShareContentVO getShareContent(String shareCode) {
        ShareLink shareLink = baseMapper.selectByShareCode(shareCode);
        
        if (shareLink == null || shareLink.getDeleted() == 1) {
            throw new BusinessException("分享链接不存在或已失效");
        }

        if (shareLink.getExpireTime() != null && shareLink.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("分享链接已过期");
        }

        ShareContentVO shareContentVO = new ShareContentVO();
        shareContentVO.setTargetType(shareLink.getTargetType());

        Integer targetType = shareLink.getTargetType();
        Long targetId = getTargetId(shareLink);

        if (targetType == 1) {
            Node node = nodeMapper.selectById(targetId);
            if (node == null) {
                throw new BusinessException("分享的文件不存在或已被删除");
            }
            shareContentVO.setTargetName(node.getNodeName());
            
            // 使用 mapper 直接查询，绕过权限验证
            MarkdownContent markdownContent = markdownContentMapper.selectById(targetId);
            shareContentVO.setContent(markdownContent != null ? markdownContent.getContent() : "");
        } else if (targetType == 0) {
            Node node = nodeMapper.selectById(targetId);
            if (node == null) {
                throw new BusinessException("分享的文件夹不存在或已被删除");
            }
            shareContentVO.setTargetName(node.getNodeName());
            NodeTreeVO treeVO = buildFolderTreePublic(targetId, node);
            shareContentVO.setContent(treeVO);
        } else if (targetType == 2) {
            Project project = projectMapper.selectById(targetId);
            if (project == null) {
                throw new BusinessException("分享的项目不存在或已被删除");
            }
            shareContentVO.setTargetName(project.getProjectName());
            NodeTreeVO treeVO = buildProjectTreePublic(targetId, project);
            shareContentVO.setContent(treeVO);
        }

        return shareContentVO;
    }

    @Override
    public String getShareNodeContent(String shareCode, Long nodeId) {
        ShareLink shareLink = baseMapper.selectByShareCode(shareCode);
        if (shareLink == null || shareLink.getDeleted() == 1) {
            throw new BusinessException("分享链接不存在或已失效");
        }
        if (shareLink.getExpireTime() != null && shareLink.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("分享链接已过期");
        }

        Node node = nodeMapper.selectById(nodeId);
        if (node == null) {
            throw new BusinessException("文件不存在");
        }
        if (node.getNodeType() != 1) {
            throw new BusinessException("该节点不是文件");
        }

        Integer targetType = shareLink.getTargetType();
        Long targetId = getTargetId(shareLink);

        if (targetType == 1) {
            if (!targetId.equals(nodeId)) {
                throw new BusinessException("无权访问该文件");
            }
        } else if (targetType == 2) {
            if (!targetId.equals(node.getProjectId())) {
                throw new BusinessException("该文件不属于分享的项目");
            }
        } else if (targetType == 0) {
            if (!node.getProjectId().equals(nodeMapper.selectById(targetId).getProjectId())) {
                throw new BusinessException("该文件不属于分享的文件夹所在项目");
            }
            if (!isDescendant(targetId, nodeId)) {
                throw new BusinessException("该文件不属于分享的文件夹");
            }
        }

        // 使用 mapper 直接查询，绕过权限验证
        MarkdownContent markdownContent = markdownContentMapper.selectById(nodeId);
        return markdownContent != null ? markdownContent.getContent() : "";
    }

    private NodeTreeVO buildProjectTreePublic(Long projectId, Project project) {
        NodeTreeVO result = new NodeTreeVO();
        result.setProjectId(projectId);
        result.setProjectName(project.getProjectName());

        LambdaQueryWrapper<Node> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Node::getProjectId, projectId)
                   .orderByAsc(Node::getCreationTime);
        
        List<Node> allNodes = nodeMapper.selectList(queryWrapper);
        
        if (allNodes.isEmpty()) {
            result.setTotalNodes(0);
            result.setFileCount(0);
            result.setFolderCount(0);
            return result;
        }
        
        List<NodeTreeVO.NodeItemVO> rootNodes = nodeTreeHelper.buildNodeTree(allNodes);
        result.setRootNodes(rootNodes);
        
        int fileCount = (int) allNodes.stream().filter(node -> node.getNodeType() == 1).count();
        int folderCount = (int) allNodes.stream().filter(node -> node.getNodeType() == 0).count();
        
        result.setTotalNodes(allNodes.size());
        result.setFileCount(fileCount);
        result.setFolderCount(folderCount);
        
        return result;
    }

    private NodeTreeVO buildFolderTreePublic(Long folderId, Node folder) {
        NodeTreeVO result = new NodeTreeVO();
        result.setProjectId(folder.getProjectId());
        result.setProjectName(folder.getNodeName());

        LambdaQueryWrapper<Node> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Node::getProjectId, folder.getProjectId())
                   .orderByAsc(Node::getCreationTime);
        List<Node> allNodes = nodeMapper.selectList(queryWrapper);

        List<NodeTreeVO.NodeItemVO> rootNodes = nodeTreeHelper.buildFolderTree(folderId, allNodes);
        if (!rootNodes.isEmpty()) {
            result.setRootNodes(rootNodes);
            int[] counts = nodeTreeHelper.countSubTree(rootNodes.get(0));
            result.setTotalNodes(counts[0] + counts[1]);
            result.setFileCount(counts[1]);
            result.setFolderCount(counts[0]);
        } else {
            result.setRootNodes(java.util.Collections.emptyList());
        }

        return result;
    }

    /**
     * 检查 childId 是否是 ancestorId 的后代
     * 简单的向上查找实现
     */
    private boolean isDescendant(Long ancestorId, Long childId) {
        if (ancestorId.equals(childId)) return true;
        
        Long currentId = childId;
        // 设置最大深度防止死循环
        int maxDepth = 100;
        int depth = 0;
        
        while (currentId != 0 && depth < maxDepth) {
            Node node = nodeMapper.selectById(currentId);
            if (node == null) return false;
            if (node.getParentId().equals(ancestorId)) return true;
            currentId = node.getParentId();
            depth++;
        }
        return false;
    }

    @Override
    public List<ShareLinkVO> getMyShareList(List<Integer> targetTypes, Long projectId, Long nodeId) {
        Long userId = UserContext.getCurrentUserId();
        LambdaQueryWrapper<ShareLink> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShareLink::getUserId, userId);
        if (targetTypes != null && !targetTypes.isEmpty()) {
            wrapper.in(ShareLink::getTargetType, targetTypes);
        }
        if (projectId != null) {
            wrapper.eq(ShareLink::getProjectId, projectId);
        }
        if (nodeId != null) {
            wrapper.eq(ShareLink::getNodeId, nodeId);
        }
        wrapper.orderByDesc(ShareLink::getCreationTime);
        List<ShareLink> shareLinks = this.list(wrapper);
        
        return shareLinks.stream().map(shareLink -> {
            ShareLinkVO shareLinkVO = shareLinkConvert.toVO(shareLink);
            shareLinkVO.setTargetName(getTargetName(shareLink.getTargetType(), getTargetId(shareLink)));
            return shareLinkVO;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteShare(Long shareId) {
        Long userId = UserContext.getCurrentUserId();
        ShareLink shareLink = this.getById(shareId);
        
        if (shareLink == null) {
            throw new BusinessException("分享链接不存在");
        }
        
        if (!shareLink.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此分享链接");
        }
        
        this.removeById(shareId);
    }

    private String generateUniqueShareCode() {
        String code;
        int attempts = 0;
        do {
            code = generateRandomCode();
            ShareLink existing = baseMapper.selectByShareCode(code);
            if (existing == null) {
                return code;
            }
            attempts++;
        } while (attempts < 10);
        throw new BusinessException("生成分享码失败，请重试");
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    @Override
    public void deleteShareByTarget(Integer targetType, Long targetId) {
        LambdaQueryWrapper<ShareLink> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShareLink::getTargetType, targetType);
        if (targetType == 2) {
            wrapper.eq(ShareLink::getProjectId, targetId);
        } else {
            wrapper.eq(ShareLink::getNodeId, targetId);
        }
        this.remove(wrapper);
    }

    @Override
    public void deleteNodeShares(List<Long> nodeIds) {
        if (nodeIds == null || nodeIds.isEmpty()) return;
        LambdaQueryWrapper<ShareLink> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ShareLink::getTargetType, java.util.Arrays.asList(0, 1))
                .in(ShareLink::getNodeId, nodeIds);
        this.remove(wrapper);
    }

    @Override
    public void restoreShareByTarget(Integer targetType, Long targetId) {
        baseMapper.restoreShareByTarget(targetType, targetId);
    }

    @Override
    public void restoreNodeShares(List<Long> nodeIds) {
        if (nodeIds != null && !nodeIds.isEmpty()) {
            baseMapper.restoreNodeShares(nodeIds);
        }
    }

    @Override
    public void physicalDeleteShareByTarget(Integer targetType, Long targetId) {
        baseMapper.physicalDeleteShareByTarget(targetType, targetId);
    }

    @Override
    public void physicalDeleteNodeShares(List<Long> nodeIds) {
        if (nodeIds != null && !nodeIds.isEmpty()) {
            baseMapper.physicalDeleteNodeShares(nodeIds);
        }
    }

    private String getTargetName(Integer targetType, Long targetId) {
        if (targetType == 1 || targetType == 0) {
            Node node = nodeMapper.selectById(targetId);
            return node != null ? node.getNodeName() : null;
        } else if (targetType == 2) {
            Project project = projectMapper.selectById(targetId);
            return project != null ? project.getProjectName() : null;
        }
        return null;
    }

    private Long getTargetId(ShareLink shareLink) {
        if (shareLink.getTargetType() == 2) {
            return shareLink.getProjectId();
        } else {
            return shareLink.getNodeId();
        }
    }

    private Long getProjectId(Integer targetType, Long targetId) {
        if (targetType == 2) {
            return targetId;
        } else {
            Node node = nodeMapper.selectById(targetId);
            return node != null ? node.getProjectId() : null;
        }
    }

    private Object toNodeItem(Node node) {
        java.util.Map<String, Object> item = new java.util.HashMap<>();
        item.put("nodeId", node.getNodeId());
        item.put("nodeName", node.getNodeName());
        item.put("nodeType", node.getNodeType());
        item.put("parentId", node.getParentId());
        return item;
    }
}
