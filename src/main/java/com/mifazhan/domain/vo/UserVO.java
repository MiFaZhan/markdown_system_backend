package com.mifazhan.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {
    private Long userId;
    private String username;
    private String email;
    private String description;
    private Integer status;
    private String token;
}
