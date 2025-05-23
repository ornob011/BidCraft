package com.dsi.hackathon.controller.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DocumentAnalysisController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentAnalysisController.class);

    @GetMapping("/project/{projectId}/analysis")
    public String getDocumentList(@PathVariable("projectId") Integer projectId,
                                  @RequestParam(required = false) Integer documentId,
                                  Model model) {



        model.addAttribute("projectId", projectId);
        model.addAttribute("documentId", documentId);
        return "views/document-analysis";
    }
}
