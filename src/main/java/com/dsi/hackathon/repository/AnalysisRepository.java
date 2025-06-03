package com.dsi.hackathon.repository;

import com.dsi.hackathon.entity.Analysis;
import com.dsi.hackathon.entity.UploadedDocument;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Analysis, Integer> {

    @Query("select a from Analysis a where a.project.id = :projectId and a.uploadedDocument.id = :documentId order by a.id limit 1")
    Optional<Analysis> findByProjectIdAndUploadedDocumentId(@Param("projectId") Integer projectId,
                                                            @Param("documentId") Integer documentId);

    @Query("select a from Analysis a where a.project.id = :id and a.uploadedDocument is null order by a.id limit 1")
    Optional<Analysis> findByProjectIdAndUploadedDocumentNull(@Param("id") Integer id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select analysis from Analysis analysis where analysis.id = :id order by analysis.id limit 1")
    Optional<Analysis> findByIdWithLock(@Param("id") Integer id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select analysis from Analysis analysis where analysis.uploadedDocument.id = :id order by analysis.id limit 1")
    Optional<Analysis> findByUploadedDocumentIdWithLock(@Param("documentId") Integer documentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select analysis from Analysis analysis where analysis.uploadedDocument in :documents order by analysis.uploadedDocument.id limit 1")
    List<Analysis> findByUploadedDocumentInWithLock(@Param("documents")Collection<UploadedDocument> documents);

    List<Analysis> findAllByUploadedDocumentId(Integer documentId);
}
