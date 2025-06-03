package com.dsi.hackathon.controller.mvc;

import com.dsi.hackathon.entity.Project;
import com.dsi.hackathon.repository.ProjectRepository;
import com.dsi.hackathon.service.ProjectService;
import com.dsi.hackathon.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ProjectController {
    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    private final ProjectRepository projectRepository;
    private final ProjectService projectService;

    public ProjectController(ProjectRepository projectRepository, ProjectService projectService) {
        this.projectRepository = projectRepository;
        this.projectService = projectService;
    }

    @GetMapping("/projects")
    public String getProjectPage(Model model) {
        Integer loggedInUserId = Utils.getLoggedInUserId();

        List<Project> projectList = projectRepository.findAllByUserId(loggedInUserId);

        model.addAttribute("projectList", projectList);

        logger.info("Accessed project page for User({})", loggedInUserId);

        return "views/project";
    }

    @GetMapping("/project/{projectId}/delete")
    public String deleteProject(@PathVariable Integer projectId) {

        projectService.delete(projectId);

        return "redirect:/";
    }
}
