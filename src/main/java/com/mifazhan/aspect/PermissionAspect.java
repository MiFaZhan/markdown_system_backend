package com.mifazhan.aspect;

import com.mifazhan.annotation.RequirePermission;
import com.mifazhan.domain.vo.RoleVO;
import com.mifazhan.domain.vo.UserVO;
import com.mifazhan.exception.BusinessException;
import com.mifazhan.service.RoleService;
import com.mifazhan.service.UserService;
import com.mifazhan.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final UserService userService;
    private final RoleService roleService;
    private final JwtUtil jwtUtil;

    @Before("@annotation(requirePermission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        String requiredPermission = requirePermission.value();
        log.info("检查权限: {}", requiredPermission);

        HttpServletRequest request = getRequest();
        String token = extractToken(request);

        if (token == null) {
            throw new BusinessException(401, "未登录");
        }

        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(401, "Token已过期或无效");
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        UserVO userVO = userService.getUserInfo(userId);

        if (userVO == null) {
            throw new BusinessException("用户不存在");
        }

        if (userVO.getStatus() != null && userVO.getStatus() == 0) {
            throw new BusinessException(403, "账号被禁用请联系管理员");
        }

        if (userVO.getRoleId() == null) {
            throw new BusinessException(403, "用户未分配角色");
        }

        RoleVO roleVO = roleService.getRoleById(userVO.getRoleId());
        if (roleVO == null) {
            throw new BusinessException(403, "角色不存在");
        }

        if (roleVO.getPermissions() == null || roleVO.getPermissions().isEmpty()) {
            throw new BusinessException(403, "角色无权限");
        }

        List<String> permissions = roleVO.getPermissions();
        if (!hasPermission(permissions, requiredPermission)) {
            throw new BusinessException(403, "无权限访问");
        }
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException("无法获取请求上下文");
        }
        return attributes.getRequest();
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean hasPermission(List<String> permissions, String requiredPermission) {
        if (permissions.contains(requiredPermission)) {
            return true;
        }

        for (String permission : permissions) {
            if (permission.endsWith(":*")) {
                String prefix = permission.substring(0, permission.length() - 1);
                if (requiredPermission.startsWith(prefix)) {
                    return true;
                }
            }
        }
        return false;
    }
}
