package com.dsi.hackathon.repository;

import com.dsi.hackathon.entity.UploadedDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UploadedDocumentRepository extends JpaRepository<UploadedDocument, Integer> {
    List<UploadedDocument> findByProjectId(Integer projectId);
}
