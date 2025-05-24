package com.dsi.hackathon.controller.rest;

import com.dsi.hackathon.entity.Analysis;
import com.dsi.hackathon.pojo.ApiResponse;
import com.dsi.hackathon.service.AnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController("/api")
public class AnalysisRestController {
    private static final Logger logger = LoggerFactory.getLogger(AnalysisRestController.class);
    private final AnalysisService analysisService;

    public AnalysisRestController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping("/analysis/{analysisId}/summary")
    public ApiResponse<String> getAnalysisSummary(@PathVariable("analysisId") Integer analysisId) {
        logger.info("Getting analysis summary for analysis id {}", analysisId);

        Analysis analysis = analysisService.analyse(analysisId);

        return ApiResponse.ok(analysis.getSummary());
    }
}
