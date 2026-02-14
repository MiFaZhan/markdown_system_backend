package com.mifazhan.domain.dto;

import lombok.Data;

@Data
public class UserListDTO {
    private String keyword;

    private Integer status;

    private Long roleId;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
