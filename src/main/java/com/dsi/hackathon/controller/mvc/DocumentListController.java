package com.dsi.hackathon.controller.mvc;

import com.dsi.hackathon.entity.Project;
import com.dsi.hackathon.enums.UploadedDocumentType;
import com.dsi.hackathon.exception.DataNotFoundException;
import com.dsi.hackathon.repository.ProjectRepository;
import com.dsi.hackathon.repository.UploadedDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DocumentListController {
    private static final Logger logger = LoggerFactory.getLogger(DocumentListController.class);

    private final UploadedDocumentRepository uploadedDocumentRepository;
    private final ProjectRepository projectRepository;

    public DocumentListController(UploadedDocumentRepository uploadedDocumentRepository,
                                  ProjectRepository projectRepository) {
        this.uploadedDocumentRepository = uploadedDocumentRepository;
        this.projectRepository = projectRepository;
    }

    @GetMapping("/project/{projectId}/document-list")
    public String getDocumentList(@PathVariable("projectId") Integer projectId,
                                  Model model) {
        logger.info("Fetching document list for Project({})", projectId);

        Project project = projectRepository.findById(projectId)
                                           .orElseThrow(
                                               DataNotFoundException.supplier(
                                                   Project.class,
                                                   projectId
                                               )
                                           );

        model.addAttribute("project", project);
        model.addAttribute("projectId", projectId);
        model.addAttribute("documentTypes", UploadedDocumentType.values());
        model.addAttribute("files", uploadedDocumentRepository.findUploadedDocumentByProjectId(projectId));

        return "views/document-list";
    }
}
