package com.dsi.hackathon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@AllArgsConstructor
public enum ProjectTab {
    OVERVIEW("Overview"),
    CHAT("Chat"),
    DOCUMENT_LIST("Documents"),
    ANALYSIS("Analysis"),
    SETTINGS("Settings"),
    ;

    private final String label;

    public String getUrl(Integer projectId) {
        return switch (this) {
            case OVERVIEW -> UriComponentsBuilder.fromPath("/project/{projectId}/overview").buildAndExpand(projectId).toUriString();
            case CHAT -> UriComponentsBuilder.fromPath("/project/{projectId}/chat").buildAndExpand(projectId).toUriString();
            case DOCUMENT_LIST -> UriComponentsBuilder.fromPath("/project/{projectId}/document-list").buildAndExpand(projectId).toUriString();
            case ANALYSIS -> UriComponentsBuilder.fromPath("/project/{projectId}/analysis").buildAndExpand(projectId).toUriString();
            case SETTINGS -> UriComponentsBuilder.fromPath("/project/{projectId}/settings").buildAndExpand(projectId).toUriString();
        };
    }
}
