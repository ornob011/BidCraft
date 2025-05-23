package com.dsi.hackathon.controller.mvc;

import com.dsi.hackathon.entity.Project;
import com.dsi.hackathon.repository.ProjectRepository;
import com.dsi.hackathon.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    private final ProjectRepository projectRepository;

    public DashboardController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Integer loggedInUserId = Utils.getLoggedInUserId();

        List<Project> projectList = projectRepository.findAllByUserId(loggedInUserId);

        model.addAttribute("projectList", projectList);

        logger.info("Accessed dashboard page for User({})", loggedInUserId);

        return "dashboard";
    }
}
