package com.dsi.hackathon.controller.mvc;

import com.dsi.hackathon.entity.Project;
import com.dsi.hackathon.enums.UploadedDocumentType;
import com.dsi.hackathon.repository.ProjectRepository;
import com.dsi.hackathon.repository.UploadedDocumentRepository;
import com.dsi.hackathon.util.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DocumentListController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentListController.class);
    private final UploadedDocumentRepository uploadedDocumentRepository;
    private final ProjectRepository projectRepository;
    private final MessageSource messageSource;

    public DocumentListController(UploadedDocumentRepository uploadedDocumentRepository, ProjectRepository projectRepository, MessageSource messageSource) {
        this.uploadedDocumentRepository = uploadedDocumentRepository;
        this.projectRepository = projectRepository;
        this.messageSource = messageSource;
    }

    @GetMapping("/document-list/{projectId}")
    public String getDocumentList(@PathVariable("projectId") Integer projectId,
                                  HttpServletRequest request,
                                  Model model) {

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("Project not found"));

        if(!project.getUser().getId().equals(Utils.getLoggedInUserId())){
            logger.info("User is not authorized to view this project");
            Utils.setErrorMessageCode(request, messageSource, "error.unauthorized.access");
            return "redirect:/dashboard";
        }

        model.addAttribute("projectId", projectId);
        model.addAttribute("documentTypes", UploadedDocumentType.values());
        model.addAttribute("files", uploadedDocumentRepository.findUploadedDocumentByProjectId(projectId));

        return "views/document-list";
    }
}
