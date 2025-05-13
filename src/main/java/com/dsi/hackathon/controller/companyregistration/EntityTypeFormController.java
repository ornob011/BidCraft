package com.dsi.hackathon.controller.companyregistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EntityTypeFormController {
    private static final Logger logger = LoggerFactory.getLogger(EntityTypeFormController.class);

    @GetMapping("/entity-type-form")
    public String entityTypeForm(
        Model model,
        @RequestParam(required = false) String clientApplicationKey
    ) {
        logger.info("GET controller of 'entity-type-form'");

        return "views/empty";
    }

}
