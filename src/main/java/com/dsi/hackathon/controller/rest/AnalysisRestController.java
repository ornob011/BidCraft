package com.dsi.hackathon.controller.rest;

import com.dsi.hackathon.entity.Analysis;
import com.dsi.hackathon.enums.AnalysisSection;
import com.dsi.hackathon.pojo.ApiResponse;
import com.dsi.hackathon.service.AnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalysisRestController {
    private static final Logger logger = LoggerFactory.getLogger(AnalysisRestController.class);

    private final AnalysisService analysisService;

    public AnalysisRestController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping("/api/analysis/{analysisId}/summary")
    public ApiResponse<String> getAnalysisSummary(@PathVariable("analysisId") Integer analysisId,
                                                  @RequestParam(required = false) AnalysisSection section) {
        logger.info("Getting analysis summary for Analysis({})", analysisId);

        String analysisContent = analysisService.analyse(analysisId, section);

        return ApiResponse.ok(analysisContent);
    }
}
