package com.dsi.hackathon.controller.mvc;

import com.dsi.hackathon.entity.Analysis;
import com.dsi.hackathon.entity.AnalysisDetail;
import com.dsi.hackathon.entity.Project;
import com.dsi.hackathon.enums.AnalysisSection;
import com.dsi.hackathon.enums.ProjectTab;
import com.dsi.hackathon.exception.DataNotFoundException;
import com.dsi.hackathon.repository.AnalysisRepository;
import com.dsi.hackathon.repository.ProjectRepository;
import com.dsi.hackathon.service.AnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

@Controller
public class ProjectAnalysisController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectAnalysisController.class);

    private final ProjectRepository projectRepository;
    private final AnalysisRepository analysisRepository;
    private final AnalysisService analysisService;

    public ProjectAnalysisController(ProjectRepository projectRepository,
                                     AnalysisRepository analysisRepository,
                                     AnalysisService analysisService) {
        this.projectRepository = projectRepository;
        this.analysisRepository = analysisRepository;
        this.analysisService = analysisService;
    }

    @GetMapping("/project/{projectId}/analysis")
    public String getDocumentList(@PathVariable("projectId") Integer projectId,
                                  @RequestParam(required = false) AnalysisSection section,
                                  Model model) {
        logger.info("Viewing analysis for Project({})", projectId);

        Project project;
        Analysis analysis;
        AnalysisSection analysisSection = Objects.requireNonNullElse(section, AnalysisSection.GENERAL_DETAILS);

        project = projectRepository.findById(projectId).orElseThrow(DataNotFoundException::new);

        analysis = analysisRepository.findByProjectIdAndUploadedDocumentNull(projectId)
                                     .orElse(null);

        if (Objects.isNull(analysis)) {
            analysis = analysisService.generateAnalysis(project, null);
        }

        AnalysisDetail analysisDetail;
        analysisDetail = analysis.getAnalysisDetailList()
                                 .stream()
                                 .filter(detail -> analysisSection.equals(detail.getAnalysisSection()))
                                 .findFirst()
                                 .orElse(null);

        if (analysisDetail == null) {
            analysisDetail = new AnalysisDetail();
            analysisDetail.setAnalysis(analysis);
            analysisDetail.setAnalysisSection(analysisSection);
        }

        model.addAttribute("project", project);
        model.addAttribute("analysis", analysis);
        model.addAttribute("analysisDetail", analysisDetail);

        model.addAttribute("activeTab", ProjectTab.ANALYSIS);
        model.addAttribute("activeSubTab", ProjectTab.ANALYSIS_PROJECT);

        model.addAttribute("selectedSection", analysisSection);

        return "views/project-analysis";
    }
}
