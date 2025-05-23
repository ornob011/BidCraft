package com.dsi.hackathon.prompts;

import com.dsi.hackathon.enums.UploadedDocumentType;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Getter
@Service
public class AnalysisPrompts {

    @Value("classpath:/prompts/system/analyzer.st")
    private Resource analyzerSystemMsg;

    @Value("classpath:/prompts/tor-summarizer.st")
    private Resource torSummarizerTemplate;

    @Value("classpath:/prompts/rfp-summarizer.st")
    private Resource rfpSummarizerTemplate;

    public Resource getAnalysisTemplate(UploadedDocumentType documentType) {
        return switch (documentType) {
            case TERMS_OF_REFERENCE -> getTorSummarizerTemplate();
            default -> null;
        };
    }
}
