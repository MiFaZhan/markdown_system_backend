package com.mifazhan.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ShareAccessDTO {
    @ApiModelProperty(value = "访问密码（可选）")
    private String password;
}
