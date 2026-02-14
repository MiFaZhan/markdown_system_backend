package com.mifazhan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mifazhan.domain.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    @Select("SELECT p.permission_code FROM permission p " +
            "INNER JOIN role_permission rp ON p.permission_id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} AND p.deleted = 0 AND rp.deleted = 0")
    List<String> getPermissionCodesByRoleId(Long roleId);
}
