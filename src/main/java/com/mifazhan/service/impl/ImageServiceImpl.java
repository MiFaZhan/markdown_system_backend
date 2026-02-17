package com.mifazhan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mifazhan.config.FileUploadConfig;
import com.mifazhan.domain.entity.Node;
import com.mifazhan.domain.entity.Project;
import com.mifazhan.exception.BusinessException;
import com.mifazhan.mapper.NodeMapper;
import com.mifazhan.mapper.ProjectMapper;
import com.mifazhan.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
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
    private final ProjectMapper projectMapper;
    private final NodeMapper nodeMapper;

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

        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        Node node = nodeMapper.selectById(nodeId);
        if (node == null) {
            throw new BusinessException("节点不存在");
        }

        String uploadPath = fileUploadConfig.getPath();
        File imagesBaseDir = new File(uploadPath);
        // 使用项目ID作为目录名
        File projectDir = new File(imagesBaseDir, String.valueOf(projectId));
        // 使用节点ID作为目录名
        File markdownDir = new File(projectDir, String.valueOf(nodeId));

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

        // URL结构: /api/images/{projectId}/{nodeId}/{filename}
        // 注意：前端配置了 API_BASE_URL，通常为 /api，所以这里只需要返回 /images/...
        // 实际上为了统一，这里返回完整路径的前缀部分（不含host）
        // 如果 application.yml 配置了 url-prefix: /api/images，我们应该遵循这个配置
        // 但是前端 useEditor.js 中会拼接 API_BASE_URL (http://localhost:8080)，如果这里返回 /api/images/...
        // 最终 URL 会是 http://localhost:8080/api/images/...
        // 这样资源映射只需要配置 /api/images/** 即可
        String imageUrl = fileUploadConfig.getUrlPrefix() + "/" + projectId + "/" + nodeId + "/" + newFilename;
        return imageUrl;
    }

    @Override
    public void deleteNodeImages(Long projectId, Long nodeId) {
        String uploadPath = fileUploadConfig.getPath();
        File imagesBaseDir = new File(uploadPath);
        File projectDir = new File(imagesBaseDir, String.valueOf(projectId));
        File nodeDir = new File(projectDir, String.valueOf(nodeId));

        if (nodeDir.exists() && nodeDir.isDirectory()) {
            try {
                // deleteRecursively 会删除目录及其所有内容
                boolean deleted = FileSystemUtils.deleteRecursively(nodeDir);
                if (deleted) {
                    log.info("删除节点图片目录成功: {}", nodeDir.getAbsolutePath());
                } else {
                    log.warn("删除节点图片目录失败，可能目录为空或权限不足: {}", nodeDir.getAbsolutePath());
                    // 尝试再次删除空目录
                    if (nodeDir.exists() && nodeDir.delete()) {
                         log.info("强制删除空目录成功: {}", nodeDir.getAbsolutePath());
                    }
                }
            } catch (Exception e) {
                log.error("删除节点图片目录异常", e);
            }
        }
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
}
