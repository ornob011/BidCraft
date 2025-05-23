package com.dsi.hackathon.configuration;

import com.dsi.hackathon.configuration.properties.MinioProperties;
import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient(MinioProperties properties) {
        return MinioClient.builder()
                          .endpoint(properties.getUrl())
                          .credentials(properties.getAccessKey(), properties.getSecretKey())
                          .build();
    }
}

