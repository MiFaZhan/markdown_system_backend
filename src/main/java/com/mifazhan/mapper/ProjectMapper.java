package com.mifazhan.mapper;

import com.mifazhan.domain.entity.Project;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author MIFAZHAN
* @description 针对表【project(项目表)】的数据库操作Mapper
* @createDate 2026-01-12
* @Entity com.mifazhan.domain.entity.Project
*/
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {

}




