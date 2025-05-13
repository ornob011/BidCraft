package com.dsi.hackathon.controller.companyregistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BusinessDetailsFormController {
    private static final Logger logger = LoggerFactory.getLogger(BusinessDetailsFormController.class);

    @GetMapping("/business-details-form")
    public String businessDetailsForm(
        Model model,
        @RequestParam("craId") Integer craId
    ) {
        logger.info("GET controller of 'business-details-form'");

        return "views/empty";
    }

}
