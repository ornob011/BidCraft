package com.dsi.hackathon.controller.mvc;

import com.dsi.hackathon.entity.Project;
import com.dsi.hackathon.entity.UploadedDocument;
import com.dsi.hackathon.exception.DataNotFoundException;
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
    private final AnalysisService analysisService;
    private final ProjectRepository projectRepository;

    public DocumentAnalysisController(UploadedDocumentRepository uploadedDocumentRepository, AnalysisService analysisService, ProjectRepository projectRepository) {
        this.uploadedDocumentRepository = uploadedDocumentRepository;
        this.analysisService = analysisService;
        this.projectRepository = projectRepository;
    }

    @GetMapping("/project/{projectId}/summary")
    public String getDocumentList(@PathVariable("projectId") Integer projectId,
                                  @RequestParam(required = false) Integer documentId,
                                  Model model) {

        String summary = "Summery not available";
        String summaryFor;

        Project project;
        project = projectRepository.findById(projectId).orElseThrow(DataNotFoundException::new);

        if (Objects.nonNull(documentId)) {
            UploadedDocument uploadedDocument;
            uploadedDocument = uploadedDocumentRepository.findById(documentId).orElseThrow(DataNotFoundException::new);

            summaryFor = "%s: %s".formatted(
                uploadedDocument.getUploadedDocumentType().getDisplayName(),
                uploadedDocument.getAttachmentName()
            );

            // todo:: check if summary for document exists
            // todo:: generate summary if not available for document
        } else {
            summaryFor = "Project: %s".formatted(project.getName());

            // todo:: check if summary for project exists
            // todo:: generate summary if not available for project
        }

        model.addAttribute("project", project);
        model.addAttribute("projectId", projectId);
        model.addAttribute("documentId", documentId);
        model.addAttribute("summaryFor", summaryFor);
        model.addAttribute("uploadedDocuments", project.getUploadedDocuments());
        model.addAttribute("summary", summary);
        return "views/document-analysis";
    }
}
