package com.mifazhan.service;

import com.mifazhan.domain.dto.MarkdownFileDTO;
import com.mifazhan.domain.entity.MarkdownFile;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mifazhan.domain.vo.MarkdownFileVO;

/**
* @author MIFAZHAN
* @description 针对表【markdown_file(Markdown 文件表)】的数据库操作Service
* @createDate 2025-12-16 15:01:03
*/
public interface MarkdownFileService extends IService<MarkdownFile> {

    MarkdownFileVO insertMarkdownFile(MarkdownFileDTO markdownFileDTO);
    
    MarkdownFileVO getMarkdownFileById(Long id);
    
    MarkdownFileVO updateMarkdownFile(Long id, MarkdownFileDTO markdownFileDTO);
    
    boolean deleteMarkdownFile(Long id);
}
