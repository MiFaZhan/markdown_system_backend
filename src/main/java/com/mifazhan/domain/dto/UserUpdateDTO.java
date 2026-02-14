package com.mifazhan.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserUpdateDTO {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private String username;

    private String email;

    private Long roleId;

    private String description;

    private Integer status;

    private String newPassword;
}
