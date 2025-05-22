package com.dsi.hackathon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashMap;

@Setter
@Getter
@Entity
@Table(name = "analysis_detail")
public class AnalysisDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", nullable = false)
    private Analysis analysis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_document_id", nullable = false)
    private UploadedDocument uploadedDocument;

    @JdbcTypeCode(SqlTypes.JSON)
    private HashMap<String, Object> mapValue = new HashMap<>();

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "AnalysisDetail{" +
               "id=" + id +
               ", analysisId=" + (analysis == null ? "null" : analysis.getId()) +
               ", uploadedDocumentId=" + (uploadedDocument == null ? "null" : uploadedDocument.getId()) +
               ", mapValue=" + mapValue +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
