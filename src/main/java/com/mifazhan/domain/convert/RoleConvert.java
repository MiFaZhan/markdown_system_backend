package com.mifazhan.domain.convert;

import com.mifazhan.domain.entity.Role;
import com.mifazhan.domain.vo.RoleVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleConvert {
    RoleVO toVO(Role role);

    List<RoleVO> toVOList(List<Role> roleList);
}
