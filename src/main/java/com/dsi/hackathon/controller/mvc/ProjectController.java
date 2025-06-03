package com.dsi.hackathon.controller.mvc;

import com.dsi.hackathon.entity.Project;
import com.dsi.hackathon.repository.ProjectRepository;
import com.dsi.hackathon.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ProjectController {
    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    private final ProjectRepository projectRepository;

    public ProjectController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @GetMapping("/projects")
    @Transactional(readOnly = true)
    public String getProjectPage(Model model) {
        Integer loggedInUserId = Utils.getLoggedInUserId();

        List<Project> projectList = projectRepository.findAllByUserId(loggedInUserId);

        model.addAttribute("projectList", projectList);

        logger.info("Accessed project page for User({})", loggedInUserId);

        return "views/project";
    }
}
