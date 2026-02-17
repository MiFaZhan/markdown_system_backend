package com.mifazhan.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    String uploadImage(MultipartFile file, Long projectId, Long nodeId);

    /**
     * 删除节点下的所有图片文件
     * @param projectId 项目ID
     * @param nodeId 节点ID
     */
    void deleteNodeImages(Long projectId, Long nodeId);
}
