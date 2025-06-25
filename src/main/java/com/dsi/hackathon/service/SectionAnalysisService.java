package com.dsi.hackathon.service;

import com.dsi.hackathon.enums.AnalysisSection;
import com.dsi.hackathon.prompts.AnalysisPrompts;
import com.dsi.hackathon.prompts.SectionAnalysisPrompts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class SectionAnalysisService {
    private static final Logger logger = LoggerFactory.getLogger(SectionAnalysisService.class);

    private final ChatClient chatClient;
    private final SectionAnalysisPrompts sectionAnalysisPrompts;

    public SectionAnalysisService(ChatClient.Builder builder,
                                  AnalysisPrompts analysisPrompts,
                                  SectionAnalysisPrompts sectionAnalysisPrompts) {
        this.chatClient = builder.defaultSystem(analysisPrompts.getAnalyzerSystemMsg()).build();
        this.sectionAnalysisPrompts = sectionAnalysisPrompts;
    }

    public String sectionAnalysis(String content, AnalysisSection analysisSection) {
        logger.debug("Section analysis for string content: AnalysisSection({})", analysisSection);

        Resource userMsgResource;
        userMsgResource = sectionAnalysisPrompts.getAnalysisTemplate(analysisSection);

        Resource markdownResource;
        markdownResource = sectionAnalysisPrompts.getMarkdownTemplate(analysisSection);

        // call api with specified prompts
        return chatClient.prompt()
                            .user(promptUserSpec -> promptUserSpec.text(userMsgResource)
                                                                  .param("document", content)
                                                                  .param("markdownTemplate", markdownResource))
                            .call()
                            .content();
    }
}
