package com.dsi.hackathon.controller.mvc;

import com.dsi.hackathon.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping(value = "/")
    public String home() {
        if (!Utils.isLoggedIn()) {
            logger.info("User is not logged in, redirecting to login page");

            return "redirect:/login";
        }

        logger.info("Redirecting to dashboard");
        return "redirect:/dashboard";
    }

}
