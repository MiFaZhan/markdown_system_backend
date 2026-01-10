package com.mifazhan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mifazhan.domain.entity.MarkdownContent;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author MIFAZHAN
 * @description 针对表【markdown_content(Markdown内容表)】的数据库操作Mapper
 */
@Mapper
public interface MarkdownContentMapper extends BaseMapper<MarkdownContent> {

}
