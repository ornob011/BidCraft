package com.dsi.hackathon.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController("/dev")
public class DevController {
    private static final Logger logger = LoggerFactory.getLogger(DevController.class);

    @PostMapping("/vector-store/add-pdf")
    public String addPdf(@RequestParam("file") MultipartFile file, String filename) {
        logger.info("add pdf file: {}", filename);



        return "success";
    }
}
