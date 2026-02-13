package com.mifazhan.domain.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

@Data
public class NodeUploadDTO {

    @NotNull(message = "上传文件不能为空")
    private MultipartFile file;

    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    private Long parentId;
}
