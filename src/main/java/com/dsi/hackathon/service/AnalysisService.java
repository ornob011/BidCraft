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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        Analysis analysis = analysisRepository.findByIdWithLock(analysisId).orElseThrow(DataNotFoundException::new);

        // For single document analysis
        if (Objects.nonNull(analysis.getUploadedDocument())) {
            analyseUploadedDocument(analysis, Boolean.FALSE);
            return analysis;
        }

        // For project analysis
        analyzeProject(analysis, Boolean.FALSE);

        return analysis;
    }

    @SuppressWarnings("UnusedReturnValue")
    @Transactional
    public boolean analyzeProject(Analysis analysis, Boolean reAnalyze) {
        Project project = analysis.getProject();

        if (Objects.nonNull(analysis.getUploadedDocument())) {
            throw new IllegalArgumentException("Analysis(%d) not targeted for project".formatted(analysis.getId()));
        }

        logger.info("Analyzing Project({}) Analysis({})", project.getId(), analysis.getId());

        List<Analysis> analysisList;
        analysisList = analysisRepository.findByUploadedDocumentInWithLock(project.getUploadedDocuments());

        boolean isProjectUpdated = false;
        for (Analysis documentAnalysis : analysisList) {
            boolean isUpdated = analyseUploadedDocument(documentAnalysis, Boolean.FALSE);
            if (isUpdated) {
                isProjectUpdated = true;
            }
        }

        if (!isProjectUpdated && Boolean.TRUE.equals(analysis.getIsAnalyzed()) && !Boolean.TRUE.equals(reAnalyze)) {
            return false;
        }

        // todo:: generate summary for project
        String documentAnalysisSummary = analysisList.stream()
                                              .map(Analysis::getSummary)
                                              .collect(Collectors.joining("\n\n"));

        String summary;
        summary = summaryAnalysisService.summaryAnalysis(documentAnalysisSummary, null);
        analysis.setSummary(summary);

        // todo:: generate analysis details for project

        analysis.setIsAnalyzed(Boolean.TRUE);
        analysis.setAnalyzedAt(LocalDateTime.now());
        return true;
    }

    @Transactional
    public boolean analyseUploadedDocument(Analysis analysis, Boolean reAnalyze) {
        UploadedDocument uploadedDocument = analysis.getUploadedDocument();

        if (Objects.isNull(uploadedDocument)) {
            throw new IllegalArgumentException("Analysis(%d) not targeted for UploadedDocument".formatted(analysis.getId()));
        }

        logger.info("Analyzing UploadedDocument({}), Analysis({})", uploadedDocument.getId(), analysis.getId());

        if (Boolean.TRUE.equals(analysis.getIsAnalyzed()) && !Boolean.TRUE.equals(reAnalyze)) {
            return false;
        }

        Resource documentResource;
        documentResource = fileUploadService.getFileResource(uploadedDocument.getFileBucket());

        String summary;
        summary = summaryAnalysisService.summaryAnalysis(documentResource, uploadedDocument.getUploadedDocumentType());
        analysis.setSummary(summary);

        // todo:: generate analysis details for document

        analysis.setIsAnalyzed(Boolean.TRUE);
        analysis.setAnalyzedAt(LocalDateTime.now());
        return true;
    }

    @Transactional
    public Analysis generateAnalysis(Project project, UploadedDocument uploadedDocument) {
        Analysis analysis = new Analysis();
        analysis.setProject(project);
        analysis.setUploadedDocument(uploadedDocument);
        return analysisRepository.save(analysis);
    }
}
