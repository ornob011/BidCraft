package com.dsi.hackathon.prompts;

import com.dsi.hackathon.enums.UploadedDocumentType;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Getter
@Service
public class AnalysisPrompts {

    @Value("classpath:/prompts/system/analyzer.st")
    private Resource analyzerSystemMsg;

    @Value("classpath:/prompts/tor-summarizer.st")
    private Resource torSummarizerTemplate;

    @Value("classpath:/prompts/rfp-summarizer.st")
    private Resource rfpSummarizerTemplate;

    @Value("classpath:/prompts/rfp/rfp-analyzer.st")
    private Resource rfpAnalyzerTemplate;

    @Value("classpath:/prompts/rfp/rfp-analysis-template.md")
    private Resource rfpAnalysisMarkdownTemplate;

    @Value("classpath:/prompts/project-summarizer.st")
    private Resource projectSummarizerTemplate;

    public Resource getAnalysisTemplate(UploadedDocumentType documentType) {

        if (Objects.isNull(documentType)) {
            return projectSummarizerTemplate;
        }

        return switch (documentType) {
            case TERMS_OF_REFERENCE -> getTorSummarizerTemplate();
            case REQUEST_FOR_PROPOSAL -> getRfpAnalyzerTemplate();
            default -> null;
        };
    }

    public Resource getMarkdownTemplate(UploadedDocumentType documentType) {

        if (Objects.isNull(documentType)) {
            return projectSummarizerTemplate;
        }

        return switch (documentType) {
            case REQUEST_FOR_PROPOSAL -> getRfpAnalysisMarkdownTemplate();
            default -> null;
        };
    }
}
