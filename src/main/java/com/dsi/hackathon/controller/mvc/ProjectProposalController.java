package com.dsi.hackathon.controller.mvc;

import com.dsi.hackathon.entity.Analysis;
import com.dsi.hackathon.entity.Project;
import com.dsi.hackathon.entity.UploadedDocument;
import com.dsi.hackathon.enums.ProjectTab;
import com.dsi.hackathon.enums.ProposalSection;
import com.dsi.hackathon.exception.DataNotFoundException;
import com.dsi.hackathon.repository.AnalysisRepository;
import com.dsi.hackathon.repository.ProjectRepository;
import com.dsi.hackathon.repository.UploadedDocumentRepository;
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
public class ProjectProposalController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectProposalController.class);

    private final UploadedDocumentRepository uploadedDocumentRepository;
    private final ProjectRepository projectRepository;
    private final AnalysisRepository analysisRepository;
    private final AnalysisService analysisService;

    public ProjectProposalController(UploadedDocumentRepository uploadedDocumentRepository,
                                     ProjectRepository projectRepository,
                                     AnalysisRepository analysisRepository,
                                     AnalysisService analysisService) {
        this.uploadedDocumentRepository = uploadedDocumentRepository;
        this.projectRepository = projectRepository;
        this.analysisRepository = analysisRepository;
        this.analysisService = analysisService;
    }

    @GetMapping("/project/{projectId}/proposal/preview")
    public String proposalPreview(@PathVariable("projectId") Integer projectId,
                                  Model model) {
        logger.info("Viewing analysis for Project({})", projectId);
        Project project;

        project = projectRepository.findById(projectId).orElseThrow(DataNotFoundException::new);

        model.addAttribute("project", project);
        model.addAttribute("activeTab", ProjectTab.PROPOSAL);
        model.addAttribute("activeSubTab", ProjectTab.PROPOSAL_PREVIEW);

        return "views/project-proposal-preview";
    }

    @GetMapping("/project/{projectId}/proposal/section")
    public String proposalSection(@PathVariable("projectId") Integer projectId,
                                  @RequestParam ProposalSection proposalSection,
                                  Model model) {
        logger.info("Viewing analysis for Project({}), ProposalSection({})", projectId, proposalSection);
        Project project;

        project = projectRepository.findById(projectId).orElseThrow(DataNotFoundException::new);

        model.addAttribute("project", project);
        model.addAttribute("activeTab", ProjectTab.PROPOSAL);
        model.addAttribute("activeSubTab", ProjectTab.PROPOSAL_SECTIONS);

        model.addAttribute("selectedSection", proposalSection);

        return "views/project-proposal-section";
    }
}
