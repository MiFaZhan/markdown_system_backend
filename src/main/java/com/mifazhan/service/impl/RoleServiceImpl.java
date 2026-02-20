package com.mifazhan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mifazhan.domain.convert.RoleConvert;
import com.mifazhan.domain.entity.Role;
import com.mifazhan.domain.vo.RoleVO;
import com.mifazhan.mapper.RoleMapper;
import com.mifazhan.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RoleConvert roleConvert;
    private final RoleMapper roleMapper;

    @Override
    public List<RoleVO> listRoles() {
        List<Role> roles = this.list();
        List<RoleVO> roleVOList = roleConvert.toVOList(roles);
        roleVOList.forEach(vo -> vo.setPermissions(roleMapper.getPermissionCodesByRoleId(vo.getRoleId())));
        return roleVOList;
    }

    @Override
    public RoleVO getRoleById(Long roleId) {
        Role role = this.getById(roleId);
        if (role == null) {
            return null;
        }
        RoleVO roleVO = roleConvert.toVO(role);
        roleVO.setPermissions(roleMapper.getPermissionCodesByRoleId(roleId));
        return roleVO;
    }

    @Override
    public RoleVO getRoleByCode(String roleCode) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleCode, roleCode);
        Role role = this.getOne(wrapper);
        if (role == null) {
            return null;
        }
        RoleVO roleVO = roleConvert.toVO(role);
        roleVO.setPermissions(roleMapper.getPermissionCodesByRoleId(role.getRoleId()));
        return roleVO;
    }
}
