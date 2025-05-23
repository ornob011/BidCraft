package com.dsi.hackathon.controller;

import com.dsi.hackathon.entity.Project;
import com.dsi.hackathon.entity.User;
import com.dsi.hackathon.repository.ProjectRepository;
import com.dsi.hackathon.repository.UserRepository;
import com.dsi.hackathon.service.PasswordHashService;
import com.dsi.hackathon.service.VectorFileService;
import com.dsi.hackathon.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

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
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public DevController(PasswordHashService passwordHashService,
                         UserRepository userRepository,
                         ProjectRepository projectRepository,
                         VectorFileService vectorFileService
    ) {
        this.passwordHashService = passwordHashService;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.vectorFileService = vectorFileService;
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
}
