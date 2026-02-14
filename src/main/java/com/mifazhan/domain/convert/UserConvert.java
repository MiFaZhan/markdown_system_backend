package com.mifazhan.domain.convert;

import com.mifazhan.domain.dto.RegisterDTO;
import com.mifazhan.domain.entity.User;
import com.mifazhan.domain.vo.LoginVO;
import com.mifazhan.domain.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserConvert {
    User toEntity(RegisterDTO registerDTO);

    UserVO toVO(User user);

    @Mapping(source = "token", target = "token")
    LoginVO toLoginVO(User user, String token);

    List<UserVO> toVOList(List<User> userList);
}
