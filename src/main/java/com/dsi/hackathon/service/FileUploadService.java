package com.dsi.hackathon.service;

import com.dsi.hackathon.configuration.properties.MinioProperties;
import com.dsi.hackathon.entity.FileBucket;
import com.dsi.hackathon.repository.FileBucketRepository;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final FileBucketRepository fileBucketRepository;


    public FileBucket uploadFile(MultipartFile file) throws Exception {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        minioClient.putObject(
            PutObjectArgs.builder()
                         .bucket(minioProperties.getBucketName())
                         .object(filename)
                         .stream(file.getInputStream(), file.getSize(), -1)
                         .contentType(file.getContentType())
                         .build()
        );

        String presignedUrl = minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                                     .method(Method.GET)
                                     .bucket(minioProperties.getBucketName())
                                     .object(filename)
//                                     .expiry(3600)   // Generate presigned URL valid for 1 hour (3600 seconds)
                                     .build()
        );

        FileBucket fileBucket = new FileBucket(){{
            setName(filename);
            setSize(Float.valueOf(file.getSize()));
            setType(file.getContentType());
            setPath(presignedUrl);
        }};

        fileBucket = fileBucketRepository.save(fileBucket);
        logger.info("File uploaded to: {}", presignedUrl);

        return fileBucket;
    }

}
