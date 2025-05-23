package com.dsi.hackathon.configuration;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MinioInitializer {

    private static final Logger logger = LoggerFactory.getLogger(MinioInitializer.class);

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @PostConstruct
    public void init() {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                logger.info("✅ Bucket created: {}", bucket);
            } else {
                logger.info("✅ Bucket already exists: {}", bucket);
            }
        } catch (Exception e) {
            logger.info("MinIO bucket setup failed: {}", e.getMessage());
        }
    }
}
