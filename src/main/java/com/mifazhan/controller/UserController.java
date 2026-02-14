package com.mifazhan.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mifazhan.annotation.RequirePermission;
import com.mifazhan.domain.dto.LoginDTO;
import com.mifazhan.domain.dto.RegisterDTO;
import com.mifazhan.domain.dto.UserListDTO;
import com.mifazhan.domain.dto.UserUpdateDTO;
import com.mifazhan.domain.vo.LoginVO;
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
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = userService.login(loginDTO);
        return Result.success("登录成功", loginVO);
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

    @GetMapping("/list")
    @RequirePermission("user:manage")
    public Result<IPage<UserVO>> listUsers(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long roleId) {
        IPage<UserVO> page = userService.listUsers(pageNum, pageSize, keyword, status, roleId);
        return Result.success(page);
    }

    @PutMapping
    @RequirePermission("user:manage")
    public Result<UserVO> updateUser(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        UserVO userVO = userService.updateUser(userUpdateDTO);
        return Result.success(userVO);
    }

    @DeleteMapping("/{userId}")
    @RequirePermission("user:manage")
    public Result<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return Result.success();
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
