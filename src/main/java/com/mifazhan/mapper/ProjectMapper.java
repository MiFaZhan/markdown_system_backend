package com.mifazhan.mapper;

import com.mifazhan.domain.entity.Project;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
* @author MIFAZHAN
* @description 针对表【project(项目表)】的数据库操作Mapper
* @createDate 2026-01-12
* @Entity com.mifazhan.domain.entity.Project
*/
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {

    @Select("<script>" +
            "SELECT * FROM project WHERE user_id = #{userId} AND deleted = 1 " +
            "<if test='keyword != null and keyword != \"\"'> AND project_name LIKE CONCAT('%', #{keyword}, '%')</if> " +
            "ORDER BY ${sortField} ${sortOrder}" +
            "</script>")
    List<Project> selectDeletedProjects(@org.apache.ibatis.annotations.Param("userId") Integer userId, 
                                      @org.apache.ibatis.annotations.Param("keyword") String keyword,
                                      @org.apache.ibatis.annotations.Param("sortField") String sortField,
                                      @org.apache.ibatis.annotations.Param("sortOrder") String sortOrder);

    @Update("UPDATE project SET deleted = 0 WHERE project_id = #{projectId}")
    void restoreProject(Long projectId);

    /**
     * 更新项目名称（忽略逻辑删除状态）
     * @param projectId 项目ID
     * @param projectName 新的项目名称
     */
    @Update("UPDATE project SET project_name = #{projectName} WHERE project_id = #{projectId}")
    void updateProjectNameIgnoringDeleted(@org.apache.ibatis.annotations.Param("projectId") Long projectId,
                                          @org.apache.ibatis.annotations.Param("projectName") String projectName);

    @Delete("DELETE FROM project WHERE project_id = #{projectId}")
    void physicalDeleteProject(Long projectId);

    @Select("SELECT * FROM project WHERE project_id = #{projectId}")
    Project selectProjectIncludingDeleted(Long projectId);

}




