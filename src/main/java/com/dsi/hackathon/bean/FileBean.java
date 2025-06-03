package com.dsi.hackathon.bean;

import lombok.Data;

@Data
public class FileBean {

    private String name;
    private String contentType;
    private byte[] fileData;

    public FileBean(String name,
                    String contentType,
                    byte[] fileData) {
        this.name = name;
        this.contentType = contentType;
        this.fileData = fileData;
    }
}
