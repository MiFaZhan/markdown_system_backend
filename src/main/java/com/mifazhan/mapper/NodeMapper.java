package com.mifazhan.mapper;

import com.mifazhan.domain.entity.Node;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
* @author MIFAZHAN
* @description 针对表【node(节点表)】的数据库操作Mapper
* @createDate 2026-01-12
* @Entity com.mifazhan.domain.entity.Node
*/
@Mapper
public interface NodeMapper extends BaseMapper<Node> {

    /**
     * 查询项目中已逻辑删除的节点
     * @param projectId 项目ID
     * @return 已删除节点列表
     */
    @Select("SELECT * FROM node WHERE project_id = #{projectId} AND deleted = 1 ORDER BY update_time DESC")
    List<Node> selectDeletedNodes(Long projectId);

    /**
     * 恢复节点（将deleted字段置为0）
     * @param nodeId 节点ID
     * @return 影响行数
     */
    @Update("UPDATE node SET deleted = 0 WHERE node_id = #{nodeId}")
    int restoreNode(Long nodeId);

    /**
     * 查询指定父节点的所有子节点（包含已逻辑删除的）
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    @Select("SELECT * FROM node WHERE parent_id = #{parentId}")
    List<Node> selectAllChildren(Long parentId);

    /**
     * 物理删除节点（彻底删除）
     * @param nodeId 节点ID
     * @return 影响行数
     */
    @Delete("DELETE FROM node WHERE node_id = #{nodeId}")
    int physicalDeleteNode(Long nodeId);

    /**
     * 根据名称查询指定父节点下的未删除节点
     * @param projectId 项目ID
     * @param parentId 父节点ID
     * @param nodeName 节点名称
     * @return 匹配的节点列表
     */
    @Select("SELECT * FROM node WHERE project_id = #{projectId} AND parent_id = #{parentId} AND node_name = #{nodeName} AND deleted = 0")
    List<Node> selectByParentIdAndName(@org.apache.ibatis.annotations.Param("projectId") Long projectId, @org.apache.ibatis.annotations.Param("parentId") Long parentId, @org.apache.ibatis.annotations.Param("nodeName") String nodeName);

    /**
     * 根据ID查询节点（包含已删除的）
     * @param nodeId 节点ID
     * @return 节点信息
     */
    @Select("SELECT * FROM node WHERE node_id = #{nodeId}")
    Node selectNodeIncludingDeleted(Long nodeId);
}




