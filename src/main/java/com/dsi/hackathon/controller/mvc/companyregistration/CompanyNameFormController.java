package com.dsi.hackathon.controller.mvc.companyregistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CompanyNameFormController {
    private static final Logger logger = LoggerFactory.getLogger(CompanyNameFormController.class);

    @GetMapping("/company-name-form")
    public String companyNameForm(
        Model model,
        @RequestParam(value = "entityTypeId", required = false) Integer entityTypeId,
        @RequestParam(value = "craId", required = false) Integer craId,
        @RequestParam(value = "liabilityTypeId", required = false) Integer liabilityTypeId,
        @RequestParam(value = "entitySubTypeId", required = false) Integer entitySubTypeId,
        @RequestParam(value = "clientApplicationKey", required = false) String clientApplicationKey
    ) {

        logger.info("GET controller of 'company-name-form' with param 01:entityTypeId = {} and param 02: liabilityTypeId: {}",
            entityTypeId, liabilityTypeId);

        return "views/empty";
    }

}
