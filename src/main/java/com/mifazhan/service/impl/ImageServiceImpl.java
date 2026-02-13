package com.mifazhan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mifazhan.config.FileUploadConfig;
import com.mifazhan.domain.entity.Node;
import com.mifazhan.domain.entity.Project;
import com.mifazhan.exception.BusinessException;
import com.mifazhan.service.ImageService;
import com.mifazhan.service.NodeService;
import com.mifazhan.service.ProjectService;
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
    private final ProjectService projectService;
    private final NodeService nodeService;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp"
    );

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp"
    );

    @Override
    public String uploadImage(MultipartFile file, Long projectId, Long nodeId) {
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

        Project project = projectService.getById(projectId);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        Node node = nodeService.getById(nodeId);
        if (node == null) {
            throw new BusinessException("节点不存在");
        }

        String projectName = sanitizeFileName(project.getProjectName());
        String markdownFileName = sanitizeFileName(removeMdExtension(node.getNodeName()));

        String uploadPath = fileUploadConfig.getPath();
        File imagesBaseDir = new File(uploadPath, "images");
        File projectDir = new File(imagesBaseDir, projectName);
        File markdownDir = new File(projectDir, markdownFileName);

        if (!markdownDir.exists()) {
            boolean created = markdownDir.mkdirs();
            if (!created) {
                throw new BusinessException("创建上传目录失败");
            }
            log.info("创建图片目录: {}", markdownDir.getAbsolutePath());
        }

        String newFilename = UUID.randomUUID().toString() + "." + extension;
        File destFile = new File(markdownDir, newFilename);

        try {
            file.transferTo(destFile);
            log.info("图片上传成功: {}", destFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("图片保存失败", e);
            throw new BusinessException("图片保存失败: " + e.getMessage());
        }

        String imageUrl = fileUploadConfig.getUrlPrefix() + "/images/" + projectName + "/" + markdownFileName + "/" + newFilename;
        return imageUrl;
    }

    private String removeMdExtension(String filename) {
        if (filename != null && filename.toLowerCase().endsWith(".md")) {
            return filename.substring(0, filename.length() - 3);
        }
        return filename;
    }

    private String sanitizeFileName(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "unnamed";
        }
        return filename.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
}
