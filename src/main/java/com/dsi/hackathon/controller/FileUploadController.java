package com.dsi.hackathon.controller;

import com.dsi.hackathon.entity.Project;
import com.dsi.hackathon.entity.UploadedDocument;
import com.dsi.hackathon.enums.UploadedDocumentType;
import com.dsi.hackathon.repository.ProjectRepository;
import com.dsi.hackathon.repository.UploadedDocumentRepository;
import com.dsi.hackathon.repository.UserRepository;
import com.dsi.hackathon.service.FileUploadService;
import com.dsi.hackathon.util.Utils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    private final FileUploadService uploadService;
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final ProjectRepository projectRepository;
    private final UploadedDocumentRepository uploadedDocumentRepository;

    @PostMapping("/upload/{projectId}")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("type") UploadedDocumentType type,
                                             @PathVariable("projectId") Integer projectId,
                                             HttpServletRequest request) throws Exception {

        String email = Utils.getAuthentication().getName();
        Integer userId = email == null ? null : userRepository.findByEmail(email).orElseThrow().getId();
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("Project not found"));

        uploadService.uploadFileAndStoreInVectorDB(file, type, project, userId);
        logger.info("Successfully added file to vector DB: {}", file.getOriginalFilename());

        Utils.setSuccessMessageCode(request, messageSource, "file.upload.successful");

        return ResponseEntity.ok("File uploaded successfully.");
    }
    
    @GetMapping("/get-files/{projectId}")
    public ResponseEntity<List<UploadedDocument>> getFiles(@PathVariable("projectId") Integer projectId) {
        List<UploadedDocument> documents = uploadedDocumentRepository.findByProjectId(projectId);
        return ResponseEntity.ok(documents);
    }


}
