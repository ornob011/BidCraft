package com.dsi.hackathon.repository;

import com.dsi.hackathon.dto.UploadedDocumentDto;
import com.dsi.hackathon.entity.UploadedDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UploadedDocumentRepository extends JpaRepository<UploadedDocument, Integer> {
    List<UploadedDocument> findByProjectId(Integer projectId);

    @Query("""
            SELECT new com.dsi.hackathon.dto.UploadedDocumentDto(
               u.id,
               u.attachmentName,
               u.uploadedDocumentType,
               u.updatedAt,
               u.fileBucket.size,
               u.fileBucket.path,
               u.project.id
            )
            FROM UploadedDocument u
            WHERE u.project.id = :projectId
        """)
    List<UploadedDocumentDto> findUploadedDocumentByProjectId(Integer projectId);
}
