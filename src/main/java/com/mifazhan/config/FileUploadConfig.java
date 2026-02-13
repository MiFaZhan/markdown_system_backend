package com.mifazhan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig {

    private String path;

    private Long maxSize = 5 * 1024 * 1024L;

    private String urlPrefix = "/api/images";
}
