package com.dsi.hackathon.controller.rest;

import com.dsi.hackathon.entity.Analysis;
import com.dsi.hackathon.entity.FileBucket;
import com.dsi.hackathon.entity.UploadedDocument;
import com.dsi.hackathon.enums.MetaDataLabel;
import com.dsi.hackathon.exception.DataNotFoundException;
import com.dsi.hackathon.repository.AnalysisRepository;
import com.dsi.hackathon.repository.FileBucketRepository;
import com.dsi.hackathon.repository.UploadedDocumentRepository;
import com.dsi.hackathon.service.DocumentService;
import com.dsi.hackathon.service.MinioCleanupService;
import com.dsi.hackathon.service.VectorFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
public class DocumentRestController {
    private static final Logger logger = LoggerFactory.getLogger(DocumentRestController.class);

    private final DocumentService documentService;
    private final UploadedDocumentRepository uploadedDocumentRepository;
    private final AnalysisRepository analysisRepository;
    private final VectorFileService vectorFileService;
    private final MinioCleanupService minioCleanupService;
    private final FileBucketRepository fileBucketRepository;

    public DocumentRestController(DocumentService documentService, UploadedDocumentRepository uploadedDocumentRepository, AnalysisRepository analysisRepository, VectorFileService vectorFileService, MinioCleanupService minioCleanupService, FileBucketRepository fileBucketRepository) {
        this.documentService = documentService;
        this.uploadedDocumentRepository = uploadedDocumentRepository;
        this.analysisRepository = analysisRepository;
        this.vectorFileService = vectorFileService;
        this.minioCleanupService = minioCleanupService;
        this.fileBucketRepository = fileBucketRepository;
    }

    @GetMapping("/api/document-viewer/{documentId}")
    public ResponseEntity<?> viewPdf(@PathVariable("documentId") Integer documentId) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = documentService.getByteArrayOutputStreamForDocument(
            documentId
        );

        logger.info("Returning PDF for UploadedDocument({})", documentId);

        return ResponseEntity.ok()
                             .contentType(MediaType.APPLICATION_PDF)
                             .body(byteArrayOutputStream.toByteArray());
    }

    @Transactional
    @DeleteMapping("/api/delete-file/{documentId}")
    public ResponseEntity<?> deleteFile(@PathVariable("documentId") Integer documentId) {
        UploadedDocument uploadedDocument = uploadedDocumentRepository.findById(documentId)
                                                                      .orElseThrow(
                                                                          DataNotFoundException.supplier(
                                                                              UploadedDocument.class,
                                                                              documentId
                                                                          )
                                                                      );

        List<Analysis> analysisList = analysisRepository.findAllByUploadedDocumentId(documentId);
        analysisRepository.deleteAll(analysisList);

        vectorFileService.deleteByMetaData(
            MetaDataLabel.UPLOADED_DOC_ID.name(),
            documentId.toString()
        );

        FileBucket fileBucket = uploadedDocument.getFileBucket();

        if (Objects.nonNull(fileBucket)) {
            minioCleanupService.deleteFileAsync(
                fileBucket.getName()
            );

            fileBucketRepository.delete(fileBucket);
        }

        uploadedDocumentRepository.delete(uploadedDocument);

        return ResponseEntity.ok().build();
    }
}
