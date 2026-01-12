package com.mifazhan.mapper;

import com.mifazhan.domain.entity.Node;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author MIFAZHAN
* @description 针对表【node(节点表)】的数据库操作Mapper
* @createDate 2026-01-12
* @Entity com.mifazhan.domain.entity.Node
*/
@Mapper
public interface NodeMapper extends BaseMapper<Node> {

}




