package com.mifazhan.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final FileUploadConfig fileUploadConfig;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = fileUploadConfig.getPath();
        
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            if (created) {
                log.info("创建图片上传目录: {}", uploadPath);
            }
        }
        
        String location = uploadPath.endsWith("/") ? uploadPath : uploadPath + "/";
        if (!location.startsWith("file:")) {
            location = "file:" + location;
        }
        
        registry.addResourceHandler("/api/images/**")
                .addResourceLocations(location);
        
        log.info("静态资源映射配置完成: /api/images/** -> {}", location);
    }
}
