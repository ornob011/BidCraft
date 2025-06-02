package com.dsi.hackathon.controller.mvc;

import com.dsi.hackathon.entity.Analysis;
import com.dsi.hackathon.entity.Project;
import com.dsi.hackathon.entity.UploadedDocument;
import com.dsi.hackathon.enums.ProjectTab;
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
public class DocumentAnalysisController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentAnalysisController.class);
    private final UploadedDocumentRepository uploadedDocumentRepository;
    private final ProjectRepository projectRepository;
    private final AnalysisRepository analysisRepository;
    private final AnalysisService analysisService;

    public DocumentAnalysisController(UploadedDocumentRepository uploadedDocumentRepository, ProjectRepository projectRepository, AnalysisRepository analysisRepository, AnalysisService analysisService) {
        this.uploadedDocumentRepository = uploadedDocumentRepository;
        this.projectRepository = projectRepository;
        this.analysisRepository = analysisRepository;
        this.analysisService = analysisService;
    }

    @GetMapping("/project/{projectId}/analysis")
    public String getDocumentList(@PathVariable("projectId") Integer projectId,
                                  @RequestParam(required = false) Integer documentId,
                                  Model model) {
        logger.info("Viewing analysis for Project({}) and UploadedDocument({})", projectId, documentId);
        String summaryFor;
        Project project;
        Analysis analysis;
        UploadedDocument uploadedDocument = null;

        project = projectRepository.findById(projectId).orElseThrow(DataNotFoundException::new);

        if (Objects.nonNull(documentId)) {
            uploadedDocument = uploadedDocumentRepository.findById(documentId).orElseThrow(DataNotFoundException::new);

            summaryFor = "%s: %s".formatted(
                uploadedDocument.getUploadedDocumentType().getDisplayName(),
                uploadedDocument.getAttachmentName()
            );

            analysis = analysisRepository.findByProjectIdAndUploadedDocumentId(projectId, documentId)
                                         .orElse(null);

        } else {
            summaryFor = "Project: %s".formatted(project.getName());

            analysis = analysisRepository.findByProjectIdAndUploadedDocumentNull(projectId)
                                         .orElse(null);
        }

        if (Objects.isNull(analysis)) {
            analysis = analysisService.generateAnalysis(project, uploadedDocument);
        }

        model.addAttribute("project", project);
        model.addAttribute("activeTab", ProjectTab.ANALYSIS);

        model.addAttribute("projectId", projectId);
        model.addAttribute("documentId", documentId);
        model.addAttribute("summaryFor", summaryFor);
        model.addAttribute("uploadedDocuments", project.getUploadedDocuments());
        model.addAttribute("analysis", analysis);
        return "views/document-analysis";
    }
}
