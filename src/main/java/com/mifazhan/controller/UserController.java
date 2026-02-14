package com.mifazhan.controller;

import com.mifazhan.domain.dto.LoginDTO;
import com.mifazhan.domain.dto.RegisterDTO;
import com.mifazhan.domain.vo.Result;
import com.mifazhan.domain.vo.UserVO;
import com.mifazhan.service.UserService;
import com.mifazhan.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterDTO registerDTO) {
        UserVO userVO = userService.register(registerDTO);
        return Result.success("注册成功", userVO);
    }

    @PostMapping("/login")
    public Result<UserVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        UserVO userVO = userService.login(loginDTO);
        return Result.success("登录成功", userVO);
    }

    @GetMapping("/info")
    public Result<UserVO> getUserInfo(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return Result.error(401, "未登录或token已过期");
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        UserVO userVO = userService.getUserInfo(userId);
        return Result.success(userVO);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
