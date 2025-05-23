package com.dsi.hackathon.configuration;

import com.dsi.hackathon.configuration.properties.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinioInitializer {

    private static final Logger logger = LoggerFactory.getLogger(MinioInitializer.class);

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @PostConstruct
    public void initBucket() throws Exception {
        boolean exists = minioClient.bucketExists(
            BucketExistsArgs.builder().bucket(minioProperties.getBucketName()).build()
        );
        if (!exists) {
            minioClient.makeBucket(
                MakeBucketArgs.builder().bucket(minioProperties.getBucketName()).build()
            );
            logger.info("Bucket '{}' created successfully.", minioProperties.getBucketName());
        } else {
            logger.info("Bucket '{}' already exists.", minioProperties.getBucketName());
        }
    }
}
