package com.mifazhan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mifazhan.domain.dto.MarkdownContentDTO;
import com.mifazhan.domain.entity.MarkdownContent;
import com.mifazhan.domain.vo.MarkdownContentVO;
import jakarta.validation.Valid;

public interface MarkdownContentService extends IService<MarkdownContent> {
    MarkdownContentVO getMarkdownContentByNodeId(Long nodeId);

    MarkdownContentVO updateMarkdownContent(Long nodeId, @Valid MarkdownContentDTO markdownContentDTO);
}
