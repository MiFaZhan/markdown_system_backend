package com.mifazhan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mifazhan.domain.entity.MarkdownContent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;
import java.util.List;

/**
 * @author MIFAZHAN
 * @description 针对表【markdown_content(Markdown内容表)】的数据库操作Mapper
 * @createDate 2026-01-12
 * @Entity com.mifazhan.domain.entity.MarkdownContent
 */
@Mapper
public interface MarkdownContentMapper extends BaseMapper<MarkdownContent> {

    /**
     * 恢复Markdown内容（将deleted字段置为0）
     * @param nodeId 节点ID
     * @return 影响行数
     */
    @Update("UPDATE markdown_content SET deleted = 0 WHERE node_id = #{nodeId}")
    int restoreContent(Long nodeId);

    @Delete("<script>DELETE FROM markdown_content WHERE node_id IN <foreach item='item' collection='nodeIds' open='(' separator=',' close=')'>#{item}</foreach></script>")
    void physicalDeleteContentByNodeIds(@Param("nodeIds") List<Long> nodeIds);

    @Update("<script>UPDATE markdown_content SET deleted = 0 WHERE node_id IN <foreach item='item' collection='nodeIds' open='(' separator=',' close=')'>#{item}</foreach></script>")
    void restoreContentByNodeIds(@Param("nodeIds") List<Long> nodeIds);
}
