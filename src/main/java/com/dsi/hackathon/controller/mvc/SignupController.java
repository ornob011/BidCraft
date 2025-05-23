package com.dsi.hackathon.controller.mvc;

import com.dsi.hackathon.entity.User;
import com.dsi.hackathon.repository.UserRepository;
import com.dsi.hackathon.service.PasswordHashService;
import com.dsi.hackathon.util.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;

import java.util.Objects;

@Controller
public class SignupController {
    private static final Logger logger = LoggerFactory.getLogger(SignupController.class);

    private final UserRepository userRepository;
    private final PasswordHashService passwordHashService;
    private final MessageSource messageSource;

    public SignupController(UserRepository userRepository,
                            PasswordHashService passwordHashService,
                            MessageSource messageSource) {
        this.userRepository = userRepository;
        this.passwordHashService = passwordHashService;
        this.messageSource = messageSource;
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());

        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(
        @Valid @ModelAttribute("user") User user,
        BindingResult bindingResult,
        Model model,
        HttpServletRequest request
    ) {
        if (Objects.isNull(user)) {
            bindingResult.reject("invalid.data");
        }

        if (ObjectUtils.isEmpty(user.getEmail())) {
            bindingResult.rejectValue(
                "email",
                "error.user.email.empty"
            );
        }

        if (ObjectUtils.isEmpty(user.getPassword())) {
            bindingResult.rejectValue(
                "password",
                "error.user.password.empty"
            );
        }

        if (userRepository.existsByEmail((user.getEmail()))) {
            bindingResult.rejectValue(
                "email",
                "error.user.already.exists"
            );
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "signup";
        }

        user.setPassword(passwordHashService.getPasswordHash(user.getPassword()));
        userRepository.save(user);

        Utils.setSuccessMessageCode(request, messageSource, "data.updated.successful");

        return "redirect:/login";
    }
}
