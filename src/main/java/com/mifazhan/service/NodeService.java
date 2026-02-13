package com.mifazhan.service;

import com.mifazhan.domain.dto.NodeDTO;
import com.mifazhan.domain.dto.NodeUpdateDTO;
import com.mifazhan.domain.dto.NodeUploadDTO;
import com.mifazhan.domain.entity.Node;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mifazhan.domain.vo.NodeVO;
import com.mifazhan.domain.vo.NodeTreeVO;
import jakarta.validation.Valid;

import java.util.List;

/**
* @author MIFAZHAN
* @description 针对表【markdown_file(Markdown 文件表)】的数据库操作Service
* @createDate 2025-12-16 15:01:03
*/
public interface NodeService extends IService<Node> {

    NodeVO insertNode(NodeDTO nodeDTO);

    List<NodeVO> listNode();

    /**
     * 获取项目的节点树形结构
     * @param projectId 项目ID
     * @return 包含项目信息和节点树的完整响应
     */
    NodeTreeVO getProjectTree(Long projectId);

    Long deleteNode(Long nodeId);

    NodeVO updateNode(@Valid NodeUpdateDTO nodeUpdateDTO);

    NodeVO uploadMarkdownFile(NodeUploadDTO nodeUploadDTO);
}
