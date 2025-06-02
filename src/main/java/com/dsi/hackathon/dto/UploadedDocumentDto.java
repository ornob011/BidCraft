package com.dsi.hackathon.dto;

import com.dsi.hackathon.enums.UploadedDocumentType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UploadedDocumentDto {
    private Integer id;
    private String name;
    private UploadedDocumentType type;
    private LocalDateTime updatedAt;
    private Float size;
    private String path;
    private String readableSize;
    private Integer projectId;

    public UploadedDocumentDto(Integer id,
                               String name,
                               UploadedDocumentType type,
                               LocalDateTime updatedAt,
                               Float size,
                               String path,
                               Integer projectId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.updatedAt = updatedAt;
        this.size = size;
        this.path = path;
        this.projectId = projectId;
    }
}
