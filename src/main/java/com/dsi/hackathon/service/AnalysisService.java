package com.dsi.hackathon.service;

import com.dsi.hackathon.entity.Analysis;
import com.dsi.hackathon.entity.Project;
import com.dsi.hackathon.entity.UploadedDocument;
import com.dsi.hackathon.exception.DataNotFoundException;
import com.dsi.hackathon.repository.AnalysisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class AnalysisService {
    private static final Logger logger = LoggerFactory.getLogger(AnalysisService.class);
    private final AnalysisRepository analysisRepository;
    private final SummaryAnalysisService summaryAnalysisService;
    private final FileUploadService fileUploadService;

    public AnalysisService(AnalysisRepository analysisRepository, SummaryAnalysisService summaryAnalysisService, FileUploadService fileUploadService) {
        this.analysisRepository = analysisRepository;
        this.summaryAnalysisService = summaryAnalysisService;
        this.fileUploadService = fileUploadService;
    }

    @Transactional
    public Analysis analyse(Integer analysisId) {
        return analyseUploadedDocument(analysisId, Boolean.FALSE);
    }

    @Transactional
    public Analysis analyseUploadedDocument(Integer analysisId, Boolean reAnalyze) {
        Analysis analysis = analysisRepository.findAllByIdWithLock(analysisId).orElseThrow(DataNotFoundException::new);
        Objects.requireNonNull(analysis.getUploadedDocument(), "Uploaded document is null for analysis :" + analysisId);

        if (Boolean.TRUE.equals(analysis.getIsAnalyzed()) && !Boolean.TRUE.equals(reAnalyze)) {
            return analysis;
        }

        Resource documentResource;
        documentResource = fileUploadService.getFileResource(analysis.getUploadedDocument().getFileBucket());

        String summary;
        summary = summaryAnalysisService.summeryAnalysis(
            documentResource,
            analysis.getUploadedDocument().getUploadedDocumentType()
        );

        analysis.setSummary(summary);

        // todo:: generate analysis details

        analysis.setIsAnalyzed(Boolean.TRUE);
        analysis.setAnalyzedAt(LocalDateTime.now());
        return analysisRepository.save(analysis);
    }

    @Transactional
    public Analysis generateAnalysis(Project project, UploadedDocument uploadedDocument) {
        Analysis analysis = new Analysis();
        analysis.setProject(project);
        analysis.setUploadedDocument(uploadedDocument);
        return analysisRepository.save(analysis);
    }
}
