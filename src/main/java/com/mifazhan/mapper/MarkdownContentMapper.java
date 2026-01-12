package com.mifazhan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mifazhan.domain.entity.MarkdownContent;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author MIFAZHAN
 * @description 针对表【markdown_content(Markdown内容表)】的数据库操作Mapper
 * @createDate 2026-01-12
 * @Entity com.mifazhan.domain.entity.MarkdownContent
 */
@Mapper
public interface MarkdownContentMapper extends BaseMapper<MarkdownContent> {

}
