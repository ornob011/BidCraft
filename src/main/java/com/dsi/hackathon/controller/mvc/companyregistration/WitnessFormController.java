package com.dsi.hackathon.controller.mvc.companyregistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WitnessFormController {
    private static final Logger logger = LoggerFactory.getLogger(WitnessFormController.class);

    @GetMapping("/witness-form")
    public String witnessForm(
        Model model,
        @RequestParam("craId") Integer craId
    ) {
        logger.info("GET controller of 'witness-form'");

        return "views/empty";
    }

}
