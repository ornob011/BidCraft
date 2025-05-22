package com.dsi.hackathon.controller.mvc;

import com.dsi.hackathon.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SignupController {
    private static final Logger logger = LoggerFactory.getLogger(SignupController.class);

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());

        return "signup";
    }
}
