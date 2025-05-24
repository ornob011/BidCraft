package com.dsi.hackathon.repository;

import com.dsi.hackathon.entity.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Analysis, Integer> {

    @Query("select a from Analysis a where a.project.id = :projectId and a.uploadedDocument.id = :documentId")
    Optional<Analysis> findByProjectIdAndUploadedDocumentId(@Param("projectId") Integer projectId,
                                                            @Param("documentId") Integer documentId);

    @Query("select a from Analysis a where a.project.id = :id and a.uploadedDocument is null")
    Optional<Analysis> findByProjectIdAndUploadedDocumentNull(@Param("id") Integer id);
}
