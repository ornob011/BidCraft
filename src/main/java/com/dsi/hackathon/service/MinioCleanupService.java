package com.dsi.hackathon.service;

import com.dsi.hackathon.configuration.properties.MinioProperties;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MinioCleanupService {
    private static final Logger logger = LoggerFactory.getLogger(MinioCleanupService.class);

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public MinioCleanupService(MinioClient minioClient,
                               MinioProperties minioProperties) {
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
    }

    @Async
    public void deleteFileAsync(String filename) {
        String bucket = minioProperties.getBucketName();

        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                                                     .bucket(bucket)
                                                     .object(filename)
                                                     .build());
        } catch (Exception ex) {
            // Log, but do not rethrow
            logger.error("Failed to delete file from MinIO: {} in bucket: {}", filename, bucket, ex);
        }
    }
}
