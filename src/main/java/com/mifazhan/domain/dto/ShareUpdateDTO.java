package com.mifazhan.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShareUpdateDTO {
    @ApiModelProperty(value = "分享ID")
    @NotNull(message = "分享ID不能为空")
    private Long shareId;

    @ApiModelProperty(value = "访问密码（空字符串表示取消密码，null表示不修改）")
    private String password;

    @ApiModelProperty(value = "过期时间（null表示不修改，除非clearExpireTime为true）")
    private LocalDateTime expireTime;

    @ApiModelProperty(value = "是否清除过期时间")
    private Boolean clearExpireTime;
}
