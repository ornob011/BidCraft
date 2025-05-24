package com.dsi.hackathon.dto;

import com.dsi.hackathon.enums.UploadedDocumentType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UploadedDocumentDto {
    private Integer id;
    private String name;
    private UploadedDocumentType type;
    private LocalDateTime updatedAt;
    private Float size;
    private String path;
}
