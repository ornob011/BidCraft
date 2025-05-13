package com.dsi.hackathon.controller.mvc.companyregistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AoaFormController {
    private static final Logger logger = LoggerFactory.getLogger(AoaFormController.class);

    @GetMapping("/aoa-form")
    public String aoaForm(
        Model model,
        @RequestParam("craId") Integer craId
    ) {
        logger.info("GET controller of 'aoa-form'");

        return "views/empty";
    }

}
