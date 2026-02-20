package com.mifazhan.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShareLinkVO {
    @ApiModelProperty(value = "分享ID")
    private Long shareId;

    @ApiModelProperty(value = "分享目标类型：0=文件夹，1=文件，2=项目")
    private Integer targetType;

    @ApiModelProperty(value = "项目ID")
    private Long projectId;

    @ApiModelProperty(value = "节点ID")
    private Long nodeId;

    @ApiModelProperty(value = "目标名称")
    private String targetName;

    @ApiModelProperty(value = "分享码")
    private String shareCode;

    @ApiModelProperty(value = "是否有密码")
    private Boolean hasPassword;

    @ApiModelProperty(value = "过期时间")
    private LocalDateTime expireTime;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime creationTime;
}
