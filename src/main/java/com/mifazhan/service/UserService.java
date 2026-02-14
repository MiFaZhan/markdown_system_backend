package com.mifazhan.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mifazhan.domain.dto.LoginDTO;
import com.mifazhan.domain.dto.RegisterDTO;
import com.mifazhan.domain.dto.UserUpdateDTO;
import com.mifazhan.domain.entity.User;
import com.mifazhan.domain.vo.LoginVO;
import com.mifazhan.domain.vo.UserVO;

public interface UserService extends IService<User> {
    UserVO register(RegisterDTO registerDTO);

    LoginVO login(LoginDTO loginDTO);

    UserVO getUserInfo(Long userId);

    IPage<UserVO> listUsers(Integer pageNum, Integer pageSize, String keyword, Integer status, Long roleId);

    UserVO updateUser(UserUpdateDTO userUpdateDTO);

    void deleteUser(Long userId);
}
