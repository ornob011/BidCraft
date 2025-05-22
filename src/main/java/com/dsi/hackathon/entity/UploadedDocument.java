package com.dsi.hackathon.entity;

import com.dsi.hackathon.enums.UploadedDocumentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "uploaded_document")
public class UploadedDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "uploaded_document_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private UploadedDocumentType uploadedDocumentType;

    private String attachmentName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_bucket_id", nullable = false)
    private FileBucket fileBucket;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "UploadedDocument{" +
               "id=" + id +
               ", projectId=" + (project == null ? "null" : project.getId()) +
               ", uploadedDocumentType=" + uploadedDocumentType +
               ", attachmentName='" + attachmentName + '\'' +
               ", fileBucketId=" + (fileBucket == null ? "null" : fileBucket.getId()) +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
