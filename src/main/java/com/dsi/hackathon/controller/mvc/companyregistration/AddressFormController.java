package com.dsi.hackathon.controller.mvc.companyregistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AddressFormController {
    private static final Logger logger = LoggerFactory.getLogger(AddressFormController.class);

    @GetMapping("/company-address-form")
    public String companyAddressForm(
        Model model,
        @RequestParam("craId") Integer craId
    ) {
        logger.info("GET controller of 'company-address-form'");

        return "views/empty";
    }

}
