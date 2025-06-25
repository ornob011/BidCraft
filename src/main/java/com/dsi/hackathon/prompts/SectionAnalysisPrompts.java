package com.dsi.hackathon.prompts;

import com.dsi.hackathon.enums.AnalysisSection;
import com.dsi.hackathon.resource.analysis.*;
import lombok.Getter;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@Service
public class SectionAnalysisPrompts {

    private final ContactInformationSar contactInformationSar;
    private final ProjectOverviewSar projectOverviewSar;
    private final TechnicalRequirementSar technicalRequirementSar;
    @Value("classpath:/prompts/system/analyzer.st")
    private Resource analyzerSystemMsg;

    @Value("classpath:/prompts/section-analysis.st")
    private Resource sectionAnalysisPrompt;

    Map<AnalysisSection, SectionAnalysisResource> sectionAnalysisResourceMap;

    public SectionAnalysisPrompts(GeneralDetailsSar generalDetailsSar, ContactInformationSar contactInformationSar, ProjectOverviewSar projectOverviewSar, TechnicalRequirementSar technicalRequirementSar) {
        sectionAnalysisResourceMap = new HashMap<>();
        sectionAnalysisResourceMap.put(AnalysisSection.GENERAL_DETAILS, generalDetailsSar);
        sectionAnalysisResourceMap.put(AnalysisSection.CONTACT_INFORMATION, contactInformationSar);
        sectionAnalysisResourceMap.put(AnalysisSection.PROJECT_OVERVIEW, projectOverviewSar);
        sectionAnalysisResourceMap.put(AnalysisSection.TECHNICAL_REQUIREMENTS, technicalRequirementSar);
        this.contactInformationSar = contactInformationSar;
        this.projectOverviewSar = projectOverviewSar;
        this.technicalRequirementSar = technicalRequirementSar;
    }

    public Resource getAnalysisTemplate(AnalysisSection analysisSection) {

        SectionAnalysisResource sectionAnalysisResource = sectionAnalysisResourceMap.get(analysisSection);

        if (Objects.isNull(sectionAnalysisResource) || Objects.isNull(sectionAnalysisResource.getSectionAnalysisPrompt())) {
            return sectionAnalysisPrompt;
        }

        return sectionAnalysisResource.getSectionAnalysisPrompt();
    }

    public Resource getMarkdownTemplate(AnalysisSection analysisSection) {

        SectionAnalysisResource sectionAnalysisResource = sectionAnalysisResourceMap.get(analysisSection);

        if (Objects.isNull(sectionAnalysisResource) || Objects.isNull(sectionAnalysisResource.getSectionAnalysisMarkdown())) {
            throw new NotImplementedException("Section analysis markdown not implemented for " + analysisSection);
        }

        return sectionAnalysisResource.getSectionAnalysisMarkdown();
    }
}
