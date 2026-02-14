package com.mifazhan.controller;

import com.mifazhan.domain.vo.Result;
import com.mifazhan.domain.vo.RoleVO;
import com.mifazhan.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/list")
    public Result<List<RoleVO>> listRoles() {
        List<RoleVO> roles = roleService.listRoles();
        return Result.success(roles);
    }

    @GetMapping
    public Result<RoleVO> getRoleById(Long roleId) {
        RoleVO roleVO = roleService.getRoleById(roleId);
        return Result.success(roleVO);
    }
}
