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
public class ProjectSettingsController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectSettingsController.class);
    private final ProjectRepository projectRepository;

    public ProjectSettingsController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @GetMapping("/project/{projectId}/settings")
    public String projectSettings(@PathVariable("projectId") Integer projectId, Model model) {

        logger.info("Viewing project settings for Project({})", projectId);
        Project project = projectRepository.findById(projectId)
                                           .orElseThrow(DataNotFoundException.supplier(Project.class, projectId));

        model.addAttribute("project", project);
        model.addAttribute("activeTab", ProjectTab.SETTINGS);

        return "views/project-settings";
    }
}
