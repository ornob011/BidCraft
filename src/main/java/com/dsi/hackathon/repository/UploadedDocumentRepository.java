package com.dsi.hackathon.repository;

import com.dsi.hackathon.entity.UploadedDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadedDocumentRepository extends JpaRepository<UploadedDocument, Integer> {
}
