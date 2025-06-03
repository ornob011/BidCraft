package com.dsi.hackathon.service;

import com.dsi.hackathon.configuration.properties.MinioProperties;
import com.dsi.hackathon.entity.FileBucket;
import com.dsi.hackathon.entity.Project;
import com.dsi.hackathon.entity.UploadedDocument;
import com.dsi.hackathon.enums.MetaDataLabel;
import com.dsi.hackathon.enums.UploadedDocumentType;
import com.dsi.hackathon.exception.FileStorageException;
import com.dsi.hackathon.repository.FileBucketRepository;
import com.dsi.hackathon.repository.UploadedDocumentRepository;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FileUploadService {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final FileBucketRepository fileBucketRepository;
    private final VectorFileService vectorFileService;
    private final UploadedDocumentRepository uploadedDocumentRepository;
    private final MinioCleanupService minioCleanupService;
    private final FileUploadService self;
    private final AnalysisService analysisService;

    public FileUploadService(MinioClient minioClient,
                             MinioProperties minioProperties,
                             FileBucketRepository fileBucketRepository,
                             VectorFileService vectorFileService,
                             UploadedDocumentRepository uploadedDocumentRepository,
                             MinioCleanupService minioCleanupService,
                             @Lazy FileUploadService self,
                             @Lazy AnalysisService analysisService) {
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
        this.fileBucketRepository = fileBucketRepository;
        this.vectorFileService = vectorFileService;
        this.uploadedDocumentRepository = uploadedDocumentRepository;
        this.minioCleanupService = minioCleanupService;
        this.self = self;
        this.analysisService = analysisService;
    }

    @Transactional
    public FileBucket uploadFile(MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // Upload to MinIO
        minioClient.putObject(
            PutObjectArgs.builder()
                         .bucket(minioProperties.getBucketName())
                         .object(filename)
                         .stream(file.getInputStream(), file.getSize(), -1)
                         .contentType(file.getContentType())
                         .build()
        );

        // Generate preSignedUrl URL
        String preSignedUrl = minioClient.getPresignedObjectUrl(
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
        fileBucket.setPath(preSignedUrl);

        try {
            return fileBucketRepository.save(fileBucket);
        } catch (Exception dbEx) {
            // Async cleanup for orphaned file
            minioCleanupService.deleteFileAsync(
                filename
            );

            // Rethrow to let the exception handler work
            throw dbEx;
        }
    }

    @Transactional
    public UploadedDocument saveUploadDocument(MultipartFile file,
                                               FileBucket fileBucket,
                                               Project project,
                                               UploadedDocumentType type) {
        UploadedDocument uploadedDocument = new UploadedDocument();

        uploadedDocument.setProject(project);
        uploadedDocument.setUploadedDocumentType(type);
        uploadedDocument.setAttachmentName(file.getOriginalFilename());
        uploadedDocument.setFileBucket(fileBucket);
        uploadedDocument = uploadedDocumentRepository.save(uploadedDocument);

        analysisService.generateAnalysis(project, uploadedDocument);

        return uploadedDocument;
    }

    @Transactional
    public void uploadFileAndStoreInVectorDB(MultipartFile file,
                                             UploadedDocumentType type,
                                             Project project) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        FileBucket fileBucket = self.uploadFile(file); // throws checked exceptions as needed
        try {
            UploadedDocument uploadedDocument = self.saveUploadDocument(file, fileBucket, project, type);

            Map<String, Object> metaData = new HashMap<>();

            metaData.put(MetaDataLabel.PROJECT_ID.name(), project.getId());
            metaData.put(MetaDataLabel.PROJECT_NAME.name(), project.getName());
            metaData.put(MetaDataLabel.FILENAME.name(), file.getOriginalFilename());
            metaData.put(MetaDataLabel.DOC_NAME.name(), type.name());
            metaData.put(MetaDataLabel.UPLOADED_DOC_ID.name(), uploadedDocument.getId());

            vectorFileService.save(file, metaData);
        } catch (Exception dbEx) {
            // Only handle potential MinIO orphan cleanup here
            minioCleanupService.deleteFileAsync(
                fileBucket.getName()
            );

            throw dbEx; // Rethrow to let the exception handler work
        }
    }

    public InputStream getFileStream(FileBucket fileBucket) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.getObject(
            GetObjectArgs.builder()
                         .bucket(minioProperties.getBucketName())
                         .object(fileBucket.getName())
                         .build()
        );
    }

    public Resource getFileResource(FileBucket fileBucket) {
        try {
            return new InputStreamResource(getFileStream(fileBucket));
        } catch (ServerException |
                 InsufficientDataException |
                 ErrorResponseException |
                 IOException |
                 NoSuchAlgorithmException |
                 InvalidKeyException |
                 InvalidResponseException |
                 XmlParserException |
                 InternalException e) {
            throw new FileStorageException("Failed to get file for FileBucket(%d)".formatted(fileBucket.getId()), e);
        }
    }
}
