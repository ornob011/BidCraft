package com.dsi.hackathon.dto;

import com.dsi.hackathon.enums.UploadedDocumentType;
import lombok.AllArgsConstructor;
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

    public UploadedDocumentDto(Integer id,
                               String name,
                               UploadedDocumentType type,
                               LocalDateTime updatedAt,
                               Float size,
                               String path) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.updatedAt = updatedAt;
        this.size = size;
        this.path = path;
    }

    public String getReadableSize() {
        String[] units = {"Bytes", "KB", "MB", "GB", "TB"};
        double size = this.size; // assuming size is in bytes
        int unitIndex = 0;
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        this.readableSize = String.format("%.2f %s", size, units[unitIndex]);
        return this.readableSize;
    }

}
