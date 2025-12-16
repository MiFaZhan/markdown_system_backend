package com.mifazhan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mifazhan.domain.MarkdownFile;
import com.mifazhan.service.MarkdownFileService;
import com.mifazhan.mapper.MarkdownFileMapper;
import org.springframework.stereotype.Service;

/**
* @author MIFAZHAN
* @description 针对表【markdown_file(Markdown 文件表)】的数据库操作Service实现
* @createDate 2025-12-16 15:01:03
*/
@Service
public class MarkdownFileServiceImpl extends ServiceImpl<MarkdownFileMapper, MarkdownFile>
    implements MarkdownFileService{

}




