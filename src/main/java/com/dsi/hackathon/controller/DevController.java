package com.dsi.hackathon.controller;

import com.dsi.hackathon.entity.UploadedDocument;
import com.dsi.hackathon.service.AnalysisService;
import com.dsi.hackathon.service.PasswordHashService;
import com.dsi.hackathon.service.VectorFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/dev")
public class DevController {
    private static final Logger logger = LoggerFactory.getLogger(DevController.class);

    private final PasswordHashService passwordHashService;
    private final VectorFileService vectorFileService;
    private final AnalysisService analysisService;

    public DevController(
        PasswordHashService passwordHashService,
        VectorFileService vectorFileService,
        AnalysisService analysisService) {
        this.passwordHashService = passwordHashService;
        this.vectorFileService = vectorFileService;
        this.analysisService = analysisService;
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

    @PostMapping("/document/summary")
    public ResponseEntity<?> generatePasswordHash(@RequestParam(required = false) Integer uploadedDocumentId,
                                                  @RequestParam(required = false) MultipartFile file) {
        logger.info("Generating UploadedDocument({}) summary", uploadedDocumentId);

        if (Objects.nonNull(uploadedDocumentId)) {
            UploadedDocument uploadedDocument;
            uploadedDocument = new UploadedDocument();
            uploadedDocument.setId(uploadedDocumentId);
            return ResponseEntity.ok(analysisService.summeryAnalysis(uploadedDocument));
        }

        if (Objects.nonNull(file)) {
            return ResponseEntity.ok(analysisService.summeryAnalysis(file));
        }

        return ResponseEntity.ok("Please provide a file or uploaded document id");
    }
}
