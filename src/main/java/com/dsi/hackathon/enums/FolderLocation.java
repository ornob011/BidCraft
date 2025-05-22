package com.dsi.hackathon.enums;

import lombok.Getter;

@Getter
public enum FolderLocation {
    ATTACHMENTS("attachments"),
    SAMPLE_DOCUMENTS("sample-documents"),
    ;

    private final String subFolderName;

    FolderLocation(String subFolderName) {
        this.subFolderName = subFolderName;
    }
}
