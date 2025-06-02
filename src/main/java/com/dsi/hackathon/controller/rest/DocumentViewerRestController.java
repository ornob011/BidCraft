package com.dsi.hackathon.controller.rest;

import com.dsi.hackathon.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
public class DocumentViewerRestController {
    private static final Logger logger = LoggerFactory.getLogger(DocumentViewerRestController.class);

    private final DocumentService documentService;

    public DocumentViewerRestController(DocumentService documentService) {
        this.documentService = documentService;
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
}
