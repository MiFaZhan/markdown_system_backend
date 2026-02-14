package com.mifazhan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mifazhan.domain.convert.UserConvert;
import com.mifazhan.domain.dto.LoginDTO;
import com.mifazhan.domain.dto.RegisterDTO;
import com.mifazhan.domain.dto.UserUpdateDTO;
import com.mifazhan.domain.entity.User;
import com.mifazhan.domain.vo.LoginVO;
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
        usernameWrapper.eq(User::getUsername, registerDTO.getUsername());
        if (this.count(usernameWrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
        emailWrapper.eq(User::getEmail, registerDTO.getEmail());
        if (this.count(emailWrapper) > 0) {
            throw new BusinessException("邮箱已被注册");
        }

        User user = userConvert.toEntity(registerDTO);
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setStatus(1);
        user.setDeleted(0);
        user.setRoleId(registerDTO.getRoleId() != null ? registerDTO.getRoleId() : 2L);

        this.save(user);

        UserVO userVO = userConvert.toVO(user);
        return userVO;
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(User::getUsername, loginDTO.getAccount())
                .or().eq(User::getEmail, loginDTO.getAccount()));

        User user = this.getOne(wrapper);
        if (user == null) {
            throw new BusinessException("账号或密码错误");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }

        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername());
        return userConvert.toLoginVO(user, token);
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        User user = this.getById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException("用户不存在");
        }
        UserVO userVO = userConvert.toVO(user);
        return userVO;
    }

    @Override
    public IPage<UserVO> listUsers(Integer pageNum, Integer pageSize, String keyword, Integer status, Long roleId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getEmail, keyword));
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        if (roleId != null) {
            wrapper.eq(User::getRoleId, roleId);
        }

        pageNum = pageNum != null ? pageNum : 1;
        pageSize = pageSize != null ? pageSize : 10;
        IPage<User> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        return page.convert(user -> userConvert.toVO(user));
    }

    @Override
    public UserVO updateUser(UserUpdateDTO userUpdateDTO) {
        User user = this.getById(userUpdateDTO.getUserId());
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException("用户不存在");
        }

        if (userUpdateDTO.getUsername() != null && !userUpdateDTO.getUsername().equals(user.getUsername())) {
            LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
            usernameWrapper.eq(User::getUsername, userUpdateDTO.getUsername())
                    .ne(User::getUserId, userUpdateDTO.getUserId());
            if (this.count(usernameWrapper) > 0) {
                throw new BusinessException("用户名已存在");
            }
            user.setUsername(userUpdateDTO.getUsername());
        }

        if (userUpdateDTO.getEmail() != null && !userUpdateDTO.getEmail().equals(user.getEmail())) {
            LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
            emailWrapper.eq(User::getEmail, userUpdateDTO.getEmail())
                    .ne(User::getUserId, userUpdateDTO.getUserId());
            if (this.count(emailWrapper) > 0) {
                throw new BusinessException("邮箱已被注册");
            }
            user.setEmail(userUpdateDTO.getEmail());
        }

        if (userUpdateDTO.getRoleId() != null) {
            user.setRoleId(userUpdateDTO.getRoleId());
        }

        if (userUpdateDTO.getStatus() != null) {
            user.setStatus(userUpdateDTO.getStatus());
        }

        if (userUpdateDTO.getDescription() != null) {
            user.setDescription(userUpdateDTO.getDescription());
        }

        if (userUpdateDTO.getNewPassword() != null && !userUpdateDTO.getNewPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userUpdateDTO.getNewPassword()));
        }

        this.updateById(user);

        User updatedUser = this.getById(user.getUserId());
        UserVO userVO = userConvert.toVO(updatedUser);
        return userVO;
    }

    @Override
    public void deleteUser(Long userId) {
        boolean result = this.removeById(userId);
        if (!result) {
            throw new BusinessException("用户不存在");
        }
    }
}
