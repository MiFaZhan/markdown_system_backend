package com.mifazhan.util;

import com.mifazhan.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class UserContext {

    private static JwtUtil jwtUtil;

    @Autowired
    public void setJwtUtil(JwtUtil jwtUtil) {
        UserContext.jwtUtil = jwtUtil;
    }

    public static Long getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException("无法获取请求上下文");
        }

        HttpServletRequest request = attributes.getRequest();
        String token = extractToken(request);

        if (token == null) {
            throw new BusinessException(401, "未登录");
        }

        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(401, "Token已过期或无效");
        }

        return jwtUtil.getUserIdFromToken(token);
    }

    private static String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
