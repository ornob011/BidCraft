package com.dsi.hackathon.controller;

import com.dsi.hackathon.entity.UploadedDocument;
import com.dsi.hackathon.enums.UploadedDocumentType;
import com.dsi.hackathon.exception.DataNotFoundException;
import com.dsi.hackathon.repository.UploadedDocumentRepository;
import com.dsi.hackathon.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/dev")
public class DevController {
    private static final Logger logger = LoggerFactory.getLogger(DevController.class);

    private final PasswordHashService passwordHashService;
    private final VectorFileService vectorFileService;
    private final SummaryAnalysisService summaryAnalysisService;
    private final UploadedDocumentRepository uploadedDocumentRepository;
    private final VectorDocumentQueryService vectorDocumentQueryService;
    private final MinioCleanupService minioCleanupService;

    public DevController(PasswordHashService passwordHashService,
                         VectorFileService vectorFileService,
                         SummaryAnalysisService summaryAnalysisService,
                         UploadedDocumentRepository uploadedDocumentRepository,
                         VectorDocumentQueryService vectorDocumentQueryService,
                         MinioCleanupService minioCleanupService) {
        this.passwordHashService = passwordHashService;
        this.vectorFileService = vectorFileService;
        this.summaryAnalysisService = summaryAnalysisService;
        this.uploadedDocumentRepository = uploadedDocumentRepository;
        this.vectorDocumentQueryService = vectorDocumentQueryService;
        this.minioCleanupService = minioCleanupService;
    }

    @PostMapping("/vector-store/add-file")
    public ResponseEntity<?> addPdf(@RequestParam("file") MultipartFile file,
                                    @RequestParam(required = false) Map<String, Object> metaData) {

        String filename = file.getOriginalFilename();
        logger.info("Adding file to vector DB: {}", filename);

        vectorFileService.save(file, metaData);

        logger.info("Successfully added file to vector DB: {}", filename);

        return ResponseEntity.ok("Successfully added file: %s".formatted(filename));
    }

    @GetMapping("/generate/password-hash")
    public ResponseEntity<?> generatePasswordHash(@RequestParam String password) {
        logger.info("Getting password hash for password: {}", password);

        String passwordHash = passwordHashService.getPasswordHash(password);

        return ResponseEntity.ok(passwordHash);
    }

    @Transactional
    @GetMapping("/delete-documents/{projectId}")
    public ResponseEntity<?> deleteAllDocuments(@PathVariable Integer projectId) {
        List<UploadedDocument> documents = uploadedDocumentRepository.findByProjectId(projectId);

        for (UploadedDocument document : documents) {
            minioCleanupService.deleteFileAsync(
                document.getFileBucket().getName()
            );

            uploadedDocumentRepository.delete(document);
        }

        return ResponseEntity.ok("Successfully deleted all documents");
    }

    @PostMapping("/document/summary")
    public ResponseEntity<?> generateDocumentSummary(@RequestParam(required = false) Integer uploadedDocumentId,
                                                     @RequestParam(required = false) MultipartFile file) {
        logger.info("Generating UploadedDocument({}) summary", uploadedDocumentId);

        if (Objects.nonNull(uploadedDocumentId)) {
            UploadedDocument uploadedDocument;
            uploadedDocument = new UploadedDocument();
            uploadedDocument.setId(uploadedDocumentId);
            return ResponseEntity.ok(summaryAnalysisService.summaryAnalysis(uploadedDocument));
        }

        if (Objects.nonNull(file)) {
            return ResponseEntity.ok(summaryAnalysisService.summaryAnalysis(file, UploadedDocumentType.TERMS_OF_REFERENCE));
        }

        return ResponseEntity.badRequest()
                             .body("Please provide a file or uploaded document id");
    }

    @GetMapping("/document-exists/{documentId}")
    public ResponseEntity<Boolean> isDocumentExistsInVectorDB(@PathVariable Integer documentId) {
        UploadedDocument document = uploadedDocumentRepository.findById(documentId)
                                                              .orElseThrow(DataNotFoundException::new);

        return ResponseEntity.ok(!vectorDocumentQueryService.getDocuments(document).isEmpty());
    }
}
