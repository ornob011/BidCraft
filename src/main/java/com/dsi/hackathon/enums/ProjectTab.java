package com.dsi.hackathon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Getter
@AllArgsConstructor
public enum ProjectTab {
    OVERVIEW("Overview"),
    CHAT("Queries"),
    DOCUMENT_LIST("Documents"),
    ANALYSIS("Analysis"),
    ANALYSIS_PROJECT("Project Analysis"),
    ANALYSIS_DOCUMENT("Document Analysis"),
    PROPOSAL("Proposal"),
    PROPOSAL_PREVIEW("Proposal Preview"),
    PROPOSAL_SECTIONS("Proposal Sections"),
    COMPLIANCE_VERIFICATION("Compliance Verification"),
    SETTINGS("Settings"),
    ;

    private final String label;

    public String getUrl(Integer projectId) {
        return switch (this) {
            case OVERVIEW -> UriComponentsBuilder.fromPath("/project/{projectId}/overview").buildAndExpand(projectId).toUriString();
            case CHAT -> UriComponentsBuilder.fromPath("/project/{projectId}/chat").buildAndExpand(projectId).toUriString();
            case DOCUMENT_LIST -> UriComponentsBuilder.fromPath("/project/{projectId}/document-list").buildAndExpand(projectId).toUriString();
            case ANALYSIS,
                 ANALYSIS_PROJECT -> UriComponentsBuilder.fromPath("/project/{projectId}/analysis").queryParam("section", AnalysisSection.GENERAL_DETAILS).buildAndExpand(projectId).toUriString();
            case ANALYSIS_DOCUMENT -> UriComponentsBuilder.fromPath("/project/{projectId}/analysis/document").buildAndExpand(projectId).toUriString();
            case PROPOSAL,
                 PROPOSAL_SECTIONS -> UriComponentsBuilder.fromPath("/project/{projectId}/proposal/section").queryParam("proposalSection", ProposalSection.RESOURCE_PLAN).buildAndExpand(projectId).toUriString();
            case PROPOSAL_PREVIEW -> UriComponentsBuilder.fromPath("/project/{projectId}/proposal/preview").buildAndExpand(projectId).toUriString();
            case COMPLIANCE_VERIFICATION -> UriComponentsBuilder.fromPath("/project/{projectId}/compliance-verification").buildAndExpand(projectId).toUriString();
            case SETTINGS -> UriComponentsBuilder.fromPath("/project/{projectId}/settings").buildAndExpand(projectId).toUriString();
        };
    }

    public List<ProjectTab> getSubTabs() {
        return switch (this) {
            case ANALYSIS -> List.of(ANALYSIS_PROJECT, ANALYSIS_DOCUMENT);
            case PROPOSAL -> List.of(PROPOSAL_PREVIEW, PROPOSAL_SECTIONS);
            default -> throw new IllegalStateException("Unexpected value: " + this);
        };
    }
}
