package com.mifazhan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mifazhan.domain.entity.ShareLink;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShareLinkMapper extends BaseMapper<ShareLink> {
    @Select("SELECT * FROM share_link WHERE share_code = #{shareCode}")
    ShareLink selectByShareCode(@Param("shareCode") String shareCode);

    @Update("UPDATE share_link SET view_count = view_count + 1 WHERE share_id = #{shareId}")
    int incrementViewCount(@Param("shareId") Long shareId);

    @Update("UPDATE share_link SET deleted = 0 WHERE target_type = #{targetType} AND target_id = #{targetId}")
    void restoreShareByTarget(@Param("targetType") Integer targetType, @Param("targetId") Long targetId);

    @Update("<script>" +
            "UPDATE share_link SET deleted = 0 WHERE target_type IN (0, 1) AND target_id IN " +
            "<foreach collection='nodeIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    void restoreNodeShares(@Param("nodeIds") List<Long> nodeIds);

    @Delete("DELETE FROM share_link WHERE target_type = #{targetType} AND target_id = #{targetId}")
    void physicalDeleteShareByTarget(@Param("targetType") Integer targetType, @Param("targetId") Long targetId);

    @Delete("<script>" +
            "DELETE FROM share_link WHERE target_type IN (0, 1) AND target_id IN " +
            "<foreach collection='nodeIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    void physicalDeleteNodeShares(@Param("nodeIds") List<Long> nodeIds);
}
