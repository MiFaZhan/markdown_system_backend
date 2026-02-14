package com.mifazhan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mifazhan.domain.dto.LoginDTO;
import com.mifazhan.domain.dto.RegisterDTO;
import com.mifazhan.domain.entity.User;
import com.mifazhan.domain.vo.UserVO;

public interface UserService extends IService<User> {
    UserVO register(RegisterDTO registerDTO);

    UserVO login(LoginDTO loginDTO);

    UserVO getUserInfo(Long userId);
}
