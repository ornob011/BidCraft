package com.dsi.hackathon.resource.analysis;

import lombok.Getter;
import org.springframework.core.io.Resource;

@Getter
public abstract class SectionAnalysisResource {

    protected Resource sectionAnalysisPrompt = null;

    protected Resource sectionAnalysisMarkdown = null;

}
