package com.dsi.hackathon.controller.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DocumentListController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentListController.class);

    @GetMapping("/document-list/{projectId}")
    public String getDocumentList(@PathVariable("projectId") Integer projectId,
                                  Model model) {
        model.addAttribute("projectId", projectId);
        return "views/document-list";
    }
}
