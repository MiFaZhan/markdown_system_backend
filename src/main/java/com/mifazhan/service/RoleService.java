package com.mifazhan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mifazhan.domain.entity.Role;
import com.mifazhan.domain.vo.RoleVO;

import java.util.List;

public interface RoleService extends IService<Role> {
    List<RoleVO> listRoles();

    RoleVO getRoleById(Long roleId);

    RoleVO getRoleByCode(String roleCode);
}
