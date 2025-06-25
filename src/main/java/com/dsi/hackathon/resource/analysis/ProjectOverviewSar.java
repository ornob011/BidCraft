package com.dsi.hackathon.resource.analysis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class ProjectOverviewSar extends SectionAnalysisResource {

    @Value("classpath:/prompts/sectionanalysis/base-section-analyzer.st")
    protected Resource sectionAnalysisPrompt;

    @Value("classpath:/prompts/sectionanalysis/project-overview-template.md")
    protected Resource sectionAnalysisMarkdown;

    @Override
    public Resource getSectionAnalysisPrompt() {
        return sectionAnalysisPrompt;
    }

    @Override
    public Resource getSectionAnalysisMarkdown() {
        return sectionAnalysisMarkdown;
    }
}
