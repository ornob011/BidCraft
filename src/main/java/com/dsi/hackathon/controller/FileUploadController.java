package com.dsi.hackathon.controller;

import com.dsi.hackathon.entity.FileBucket;
import com.dsi.hackathon.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/file")
public class FileUploadController {

    @Autowired
    private FileUploadService uploadService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        FileBucket fileBucket = uploadService.uploadFile(file);
        return ResponseEntity.ok("File uploaded: {}" + fileBucket);
    }

}
