package com.mifazhan.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@TableName(value = "permission")
@Data
public class Permission {
    @TableId(type = IdType.AUTO)
    private Long permissionId;

    private String permissionCode;

    private String permissionName;

    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime creationTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
