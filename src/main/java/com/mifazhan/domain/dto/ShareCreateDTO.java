package com.mifazhan.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShareCreateDTO {
    @ApiModelProperty(value = "分享目标类型：0=文件夹，1=文件，2=项目")
    @NotNull(message = "分享目标类型不能为空")
    private Integer targetType;

    @ApiModelProperty(value = "目标ID")
    @NotNull(message = "目标ID不能为空")
    private Long targetId;

    @ApiModelProperty(value = "访问密码（可选）")
    private String password;

    @ApiModelProperty(value = "过期时间（可选）")
    private LocalDateTime expireTime;
}
