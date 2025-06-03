package com.dsi.hackathon.service;

import com.dsi.hackathon.bean.FileBean;
import com.dsi.hackathon.configuration.properties.MinioProperties;
import com.dsi.hackathon.entity.FileBucket;
import com.dsi.hackathon.entity.UploadedDocument;
import com.dsi.hackathon.exception.DataNotFoundException;
import com.dsi.hackathon.exception.DocumentStorageException;
import com.dsi.hackathon.repository.FileBucketRepository;
import com.dsi.hackathon.repository.UploadedDocumentRepository;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
public class DocumentService {
    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    private final UploadedDocumentRepository uploadedDocumentRepository;
    private final FileBucketRepository fileBucketRepository;
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public DocumentService(UploadedDocumentRepository uploadedDocumentRepository,
                           FileBucketRepository fileBucketRepository,
                           MinioClient minioClient,
                           MinioProperties minioProperties) {
        this.uploadedDocumentRepository = uploadedDocumentRepository;
        this.fileBucketRepository = fileBucketRepository;
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
    }

    public ByteArrayOutputStream getByteArrayOutputStreamForDocument(Integer documentId) throws IOException {
        byte[] fileData = getDocumentByteArray(documentId).orElseThrow(DataNotFoundException.supplier(
                UploadedDocument.class,
                documentId
            )
        );

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        byteArrayOutputStream.write(fileData);

        return byteArrayOutputStream;
    }

    private Optional<byte[]> getDocumentByteArray(Integer documentId) {
        logger.debug("Generating Document byte array for UploadedDocument({})", documentId);

        UploadedDocument uploadedDocument = uploadedDocumentRepository.findById(documentId)
                                                                      .orElseThrow(
                                                                          DataNotFoundException.supplier(
                                                                              UploadedDocument.class,
                                                                              documentId
                                                                          )
                                                                      );

        FileBucket fileBucket = uploadedDocument.getFileBucket();

        if (fileBucket == null) {
            logger.warn("No file bucket associated with UploadedDocument({})", documentId);
            return Optional.empty();
        }

        FileBean fileBean = getFile(fileBucket.getName());

        return Optional.ofNullable(fileBean.getFileData());
    }

    public FileBean getFile(String fileName) {
        FileBucket fileBucket = fileBucketRepository.findByName(fileName)
                                                    .orElseThrow(
                                                        DataNotFoundException.supplier(
                                                            FileBucket.class,
                                                            fileName
                                                        )
                                                    );

        logger.debug("File Bucket found using fileName({}), fileBucketId({})", fileName, fileBucket.getId());

        try (InputStream inputStream = minioClient.getObject(
            GetObjectArgs.builder()
                         .bucket(minioProperties.getBucketName())
                         .object(fileName)
                         .build()
        )) {
            String contentType = fileBucket.getType();
            byte[] fileBytes = inputStream.readAllBytes();

            return new FileBean(fileName, contentType, fileBytes);

        } catch (Exception e) {
            throw new DocumentStorageException("Failed to retrieve file from MinIO: " + fileName, e);
        }
    }
}
