package com.dsi.hackathon.service;

import com.dsi.hackathon.configuration.properties.MinioProperties;
import com.dsi.hackathon.entity.FileBucket;
import com.dsi.hackathon.entity.Project;
import com.dsi.hackathon.entity.UploadedDocument;
import com.dsi.hackathon.enums.MetaDataLabel;
import com.dsi.hackathon.enums.UploadedDocumentType;
import com.dsi.hackathon.repository.FileBucketRepository;
import com.dsi.hackathon.repository.UploadedDocumentRepository;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final FileBucketRepository fileBucketRepository;
    private final VectorFileService vectorFileService;
    private final UploadedDocumentRepository uploadedDocumentRepository;


    @Transactional
    public FileBucket uploadFile(MultipartFile file) throws Exception {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            // Upload to MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(filename)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // Generate presigned URL
            String presignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioProperties.getBucketName())
                            .object(filename)
                            .build()
            );

            // Save metadata to DB
            FileBucket fileBucket = new FileBucket();
            fileBucket.setName(filename);
            fileBucket.setSize((float) file.getSize());
            fileBucket.setType(file.getContentType());
            fileBucket.setPath(presignedUrl);

            // Save to DB
            fileBucket = fileBucketRepository.save(fileBucket);

            logger.info("File uploaded to: {}", presignedUrl);
            return fileBucket;

        } catch (Exception e) {
            // Rollback MinIO upload if DB save fails
            try {
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(filename)
                        .build());
                logger.info("Rolled back MinIO file: {}", filename);
            } catch (Exception minioEx) {
                logger.error("Failed to delete file from MinIO after DB failure: {}", minioEx.getMessage());
            }
            throw e; // rethrow the original exception
        }
    }


    @Transactional
    public UploadedDocument saveUploadDocument(MultipartFile file, FileBucket fileBucket, Project project, UploadedDocumentType type){
        UploadedDocument uploadedDocument = new UploadedDocument();
        uploadedDocument.setProject(project);
        uploadedDocument.setUploadedDocumentType(type);
        uploadedDocument.setAttachmentName(file.getOriginalFilename());
        uploadedDocument.setFileBucket(fileBucket);
        uploadedDocument = uploadedDocumentRepository.save(uploadedDocument);
        return uploadedDocument;
    }

    @Transactional
    public void uploadFileAndStoreInVectorDB (MultipartFile file, UploadedDocumentType type, Project project, Integer userId) throws Exception {

        FileBucket fileBucket = uploadFile(file);
        try {
            UploadedDocument uploadedDocument = saveUploadDocument(file, fileBucket, project, type);

            Map<String, Object> metaData = new HashMap<>();
            metaData.put(MetaDataLabel.PROJECT_ID.name(), project.getId());
            metaData.put(MetaDataLabel.PROJECT_NAME.name(), project.getName());
            metaData.put(MetaDataLabel.FILENAME.name(), file.getOriginalFilename());
            metaData.put(MetaDataLabel.DOC_NAME.name(), type.name());
            metaData.put(MetaDataLabel.UPLOADED_DOC_ID.name(), uploadedDocument.getId());

            vectorFileService.save(file, metaData);
        } catch (Exception e) {
            // Rollback MinIO upload if DB save fails
            try {
                minioClient.removeObject(RemoveObjectArgs.builder()
                                                         .bucket(minioProperties.getBucketName())
                                                         .object(fileBucket.getName())
                                                         .build());
                logger.info("Rolled back MinIO file: {}", fileBucket.getName());
            } catch (Exception minioEx) {
                logger.error("Failed to delete file from MinIO after DB failure: {}", minioEx.getMessage());
            }
            throw e; // rethrow the original exception
        }
    }

}
