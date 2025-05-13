package com.dsi.hackathon.controller.companyregistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ShareHolderPositionFormController {
    private static final Logger logger = LoggerFactory.getLogger(ShareHolderPositionFormController.class);

    @GetMapping("/share-holder-position-form")
    public String shareHolderList(
        Model model,
        @RequestParam("craId") Integer craId
    ) {
        logger.info("GET controller of 'share-holder-position'");

        return "views/empty";
    }
}
