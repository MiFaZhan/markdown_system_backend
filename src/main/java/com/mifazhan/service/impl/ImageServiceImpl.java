package com.mifazhan.service.impl;

import com.mifazhan.config.FileUploadConfig;
import com.mifazhan.exception.BusinessException;
import com.mifazhan.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final FileUploadConfig fileUploadConfig;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp"
    );

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp"
    );

    @Override
    public String uploadImage(MultipartFile file, Long projectId) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BusinessException("文件名不能为空");
        }

        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BusinessException("不支持的文件类型，仅支持: " + String.join(", ", ALLOWED_EXTENSIONS));
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BusinessException("不支持的文件内容类型");
        }

        if (file.getSize() > fileUploadConfig.getMaxSize()) {
            throw new BusinessException("文件大小超过限制，最大允许: " + (fileUploadConfig.getMaxSize() / 1024 / 1024) + "MB");
        }

        String uploadPath = fileUploadConfig.getPath();
        File projectDir = new File(uploadPath, String.valueOf(projectId));
        if (!projectDir.exists()) {
            boolean created = projectDir.mkdirs();
            if (!created) {
                throw new BusinessException("创建上传目录失败");
            }
            log.info("创建项目图片目录: {}", projectDir.getAbsolutePath());
        }

        String newFilename = UUID.randomUUID().toString() + "." + extension;
        File destFile = new File(projectDir, newFilename);

        try {
            file.transferTo(destFile);
            log.info("图片上传成功: {}", destFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("图片保存失败", e);
            throw new BusinessException("图片保存失败: " + e.getMessage());
        }

        String imageUrl = fileUploadConfig.getUrlPrefix() + "/" + projectId + "/" + newFilename;
        return imageUrl;
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
}
