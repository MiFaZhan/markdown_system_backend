package com.mifazhan.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@TableName(value = "role")
@Data
public class Role {
    @TableId(type = IdType.AUTO)
    private Long roleId;

    private String roleName;

    private String roleCode;

    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime creationTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
