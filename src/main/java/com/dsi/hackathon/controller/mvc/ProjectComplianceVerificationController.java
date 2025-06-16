package com.dsi.hackathon.controller.mvc;

import com.dsi.hackathon.entity.Project;
import com.dsi.hackathon.enums.ProjectTab;
import com.dsi.hackathon.exception.DataNotFoundException;
import com.dsi.hackathon.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProjectComplianceVerificationController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectComplianceVerificationController.class);

    private final ProjectRepository projectRepository;

    public ProjectComplianceVerificationController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @GetMapping("/project/{projectId}/compliance-verification")
    public String getDocumentList(@PathVariable("projectId") Integer projectId,
                                  Model model) {
        logger.info("Viewing compliance-verification page for Project({})", projectId);

        Project project;
        project = projectRepository.findById(projectId)
                                   .orElseThrow(DataNotFoundException.supplier(Project.class, projectId));

        model.addAttribute("project", project);
        model.addAttribute("activeTab", ProjectTab.COMPLIANCE_VERIFICATION);

        return "views/project-compliance-verification";
    }
}
