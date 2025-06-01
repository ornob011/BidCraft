package com.dsi.hackathon.controller;

import com.dsi.hackathon.configuration.properties.MinioProperties;
import com.dsi.hackathon.entity.UploadedDocument;
import com.dsi.hackathon.enums.UploadedDocumentType;
import com.dsi.hackathon.repository.UploadedDocumentRepository;
import com.dsi.hackathon.service.SummaryAnalysisService;
import com.dsi.hackathon.entity.Project;
import com.dsi.hackathon.entity.User;
import com.dsi.hackathon.repository.ProjectRepository;
import com.dsi.hackathon.repository.UserRepository;
import com.dsi.hackathon.service.PasswordHashService;
import com.dsi.hackathon.service.VectorDocumentQueryService;
import com.dsi.hackathon.service.VectorFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;

import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/dev")
public class DevController {
    private static final Logger logger = LoggerFactory.getLogger(DevController.class);

    private final PasswordHashService passwordHashService;
    private final VectorFileService vectorFileService;
    private final SummaryAnalysisService summaryAnalysisService;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final UploadedDocumentRepository uploadedDocumentRepository;
    private final VectorDocumentQueryService vectorDocumentQueryService;

    public DevController(PasswordHashService passwordHashService,
                         UserRepository userRepository,
                         ProjectRepository projectRepository,
                         VectorFileService vectorFileService,
                         SummaryAnalysisService summaryAnalysisService,
                         MinioClient minioClient,
                         MinioProperties minioProperties,
                         UploadedDocumentRepository uploadedDocumentRepository,
                         VectorDocumentQueryService vectorDocumentQueryService) {
        this.passwordHashService = passwordHashService;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.vectorFileService = vectorFileService;
        this.summaryAnalysisService = summaryAnalysisService;
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
        this.uploadedDocumentRepository = uploadedDocumentRepository;
        this.vectorDocumentQueryService = vectorDocumentQueryService;
    }

    @PostMapping("/vector-store/add-file")
    public String addPdf(@RequestParam("file") MultipartFile file,
                         @RequestParam(required = false) Map<String, Object> metaData) {

        String filename = file.getOriginalFilename();
        logger.info("Adding file to vector DB: {}", filename);

        vectorFileService.save(file, metaData);

        logger.info("Successfully added file to vector DB: {}", filename);
        return "Successfully added file: " + filename;
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
            try {
                minioClient.removeObject(
                    RemoveObjectArgs.builder()
                                    .bucket(minioProperties.getBucketName())
                                    .object(document.getFileBucket().getName())
                                    .build()
                );
                uploadedDocumentRepository.delete(document);
            } catch (Exception e) {
                logger.error("Failed to delete document: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete documents");
            }
        }

        return ResponseEntity.ok("Successfully deleted all documents");
    }

    @PostMapping("/document/summary")
    public ResponseEntity<?> generatePasswordHash(@RequestParam(required = false) Integer uploadedDocumentId,
                                                  @RequestParam(required = false) MultipartFile file) {
        logger.info("Generating UploadedDocument({}) summary", uploadedDocumentId);

        if (Objects.nonNull(uploadedDocumentId)) {
            UploadedDocument uploadedDocument;
            uploadedDocument = new UploadedDocument();
            uploadedDocument.setId(uploadedDocumentId);
            return ResponseEntity.ok(summaryAnalysisService.summeryAnalysis(uploadedDocument));
        }

        if (Objects.nonNull(file)) {
            return ResponseEntity.ok(summaryAnalysisService.summeryAnalysis(file, UploadedDocumentType.TERMS_OF_REFERENCE));
        }

        return ResponseEntity.ok("Please provide a file or uploaded document id");
    }

    @Transactional
    @GetMapping("/generate-project-entry")
    public ResponseEntity<Project> createProjectEntity(@RequestParam("userId") Integer userId) {

        User user = null;

        if(userId == null){
            // Fetch users from DB
            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                throw new RuntimeException("No users found in database.");
            }
            user = users.get(new Random().nextInt(users.size()));
        } else {
            user = userRepository.findById(userId).orElseThrow();
        }

        // Create random input data
        String code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String name = "AutoName_" + new Random().nextInt(1000);
        String description = "Generated description at " + LocalDateTime.now();

        // Build entity
        Project project = new Project();
        project.setCode(code);
        project.setName(name);
        project.setDescription(description);
        project.setUser(user);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());

        // Save and return
        return new ResponseEntity<>(projectRepository.save(project), HttpStatus.CREATED);
    }

    @GetMapping("/document-exists/{documentId}")
    public ResponseEntity<Boolean> isDocumentExistsInVectorDB(@PathVariable Integer documentId) {
        UploadedDocument document = uploadedDocumentRepository.findById(documentId)
                                                              .orElseThrow(() -> new RuntimeException("Document not found"));
        return ResponseEntity.ok(!vectorDocumentQueryService.getDocuments(document).isEmpty());
    }
}
