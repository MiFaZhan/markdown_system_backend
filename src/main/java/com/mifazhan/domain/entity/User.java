package com.mifazhan.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@TableName(value = "user")
@Data
public class User {
    @TableId(type = IdType.AUTO)
    private Long userId;

    private String username;

    private String password;

    private String email;

    private String description;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime creationTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private Integer deleted;
}
