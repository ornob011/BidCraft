package com.dsi.hackathon.controller;

import com.dsi.hackathon.service.PasswordHashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/dev")
public class DevController {
    private static final Logger logger = LoggerFactory.getLogger(DevController.class);

    private final PasswordHashService passwordHashService;

    public DevController(PasswordHashService passwordHashService) {
        this.passwordHashService = passwordHashService;
    }

    @PostMapping("/vector-store/add-pdf")
    public String addPdf(@RequestParam("file") MultipartFile file, String filename) {
        logger.info("add pdf file: {}", filename);



        return "success";
    }

    @GetMapping("/generate/password-hash")
    public ResponseEntity<?> generatePasswordHash(@RequestParam String password) {
        logger.info("Getting password hash for password: {}", password);

        String passwordHash = passwordHashService.getPasswordHash(password);

        return ResponseEntity.ok(passwordHash);
    }
}
