package com.mifazhan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mifazhan.domain.convert.UserConvert;
import com.mifazhan.domain.dto.LoginDTO;
import com.mifazhan.domain.dto.RegisterDTO;
import com.mifazhan.domain.entity.User;
import com.mifazhan.domain.vo.UserVO;
import com.mifazhan.exception.BusinessException;
import com.mifazhan.mapper.UserMapper;
import com.mifazhan.service.UserService;
import com.mifazhan.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserConvert userConvert;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserVO register(RegisterDTO registerDTO) {
        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(User::getUsername, registerDTO.getUsername())
                .eq(User::getDeleted, 0);
        if (this.count(usernameWrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
        emailWrapper.eq(User::getEmail, registerDTO.getEmail())
                .eq(User::getDeleted, 0);
        if (this.count(emailWrapper) > 0) {
            throw new BusinessException("邮箱已被注册");
        }

        User user = userConvert.toEntity(registerDTO);
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setStatus(1);
        user.setDeleted(0);

        this.save(user);

        UserVO userVO = userConvert.toVO(user);
        userVO.setToken(jwtUtil.generateToken(user.getUserId(), user.getUsername()));
        return userVO;
    }

    @Override
    public UserVO login(LoginDTO loginDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginDTO.getUsername())
                .eq(User::getDeleted, 0);

        User user = this.getOne(wrapper);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        return userConvert.toVO(user, jwtUtil.generateToken(user.getUserId(), user.getUsername()));
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        User user = this.getById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException("用户不存在");
        }
        return userConvert.toVO(user);
    }
}
