package com.mifazhan.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@TableName(value = "share_link")
@Data
public class ShareLink {
    @TableId(type = IdType.AUTO)
    private Long shareId;

    private Integer targetType;

    private Long projectId;

    private Long nodeId;

    private Long userId;

    private String shareCode;

    private String password;

    private LocalDateTime expireTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime creationTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
