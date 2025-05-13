package com.dsi.hackathon.controller.mvc.companyregistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OtherAttachmentsFormController {
    private static final Logger logger = LoggerFactory.getLogger(OtherAttachmentsFormController.class);

    @GetMapping("/other-attachments-form")
    public String uploadDocumentsPage(
        Model model,
        @RequestParam("craId") Integer craId
    ) {
        logger.info("GET controller of 'other-attachments'");

        return "views/empty";
    }

}
