package com.dsi.hackathon.controller.rest;

import com.dsi.hackathon.dto.ProjectDto;
import com.dsi.hackathon.entity.Project;
import com.dsi.hackathon.entity.User;
import com.dsi.hackathon.pojo.ApiResponse;
import com.dsi.hackathon.repository.ProjectRepository;
import com.dsi.hackathon.repository.UserRepository;
import com.dsi.hackathon.util.Utils;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProjectRestController {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    public ProjectRestController(ProjectRepository projectRepository, UserRepository userRepository, MessageSource messageSource) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    @PostMapping("/create-project")
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody ProjectDto dto,
                                                       BindingResult bindingResult) {
        if (ObjectUtils.isEmpty(dto.getName())){
            bindingResult.rejectValue("name","project.name.not.blank");
        }

        if (ObjectUtils.isEmpty(dto.getDescription())){
            bindingResult.rejectValue("description","project.description.not.blank");
        }

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                                 .body(
                                     ApiResponse.badRequest(
                                         Utils.getErrorStrList(messageSource, bindingResult),
                                         Utils.getMessageFromMessageSource(messageSource, "project.creation.failed")
                                     )
                                 );
        }

        User user = userRepository.findById(Utils.getLoggedInUserId())
                                  .orElseThrow();

        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setUser(user);
        projectRepository.save(project);

        String successMessage = Utils.getMessageFromMessageSource(messageSource, "project.create.success");

        return ResponseEntity.ok(
            ApiResponse.ok(
                project.getId(),
                successMessage
            )
        );
    }
}

