package com.mifazhan.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ShareContentVO {
    @ApiModelProperty(value = "分享目标类型：0=文件夹，1=文件，2=项目")
    private Integer targetType;

    @ApiModelProperty(value = "目标名称")
    private String targetName;

    @ApiModelProperty(value = "分享内容")
    private Object content;
}
