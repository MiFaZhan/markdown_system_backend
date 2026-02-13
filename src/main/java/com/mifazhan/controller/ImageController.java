package com.mifazhan.controller;

import com.mifazhan.domain.vo.Result;
import com.mifazhan.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public Result<String> uploadImage(
            @RequestParam("file[]") MultipartFile file,
            @RequestParam("projectId") Long projectId,
            @RequestParam("nodeId") Long nodeId) {
        log.info("收到图片上传请求, projectId: {}, nodeId: {}, filename: {}", projectId, nodeId, file.getOriginalFilename());
        
        String imageUrl = imageService.uploadImage(file, projectId, nodeId);
        log.info("图片上传成功, URL: {}", imageUrl);
        
        return Result.success(imageUrl);
    }
}
