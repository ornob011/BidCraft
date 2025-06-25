package com.dsi.hackathon.service;

import com.dsi.hackathon.entity.Analysis;
import com.dsi.hackathon.entity.AnalysisDetail;
import com.dsi.hackathon.entity.Project;
import com.dsi.hackathon.entity.UploadedDocument;
import com.dsi.hackathon.enums.AnalysisSection;
import com.dsi.hackathon.exception.DataNotFoundException;
import com.dsi.hackathon.repository.AnalysisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AnalysisService {
    private static final Logger logger = LoggerFactory.getLogger(AnalysisService.class);

    private final AnalysisRepository analysisRepository;
    private final SummaryAnalysisService summaryAnalysisService;
    private final FileUploadService fileUploadService;
    private final SectionAnalysisService sectionAnalysisService;

    public AnalysisService(AnalysisRepository analysisRepository,
                           SummaryAnalysisService summaryAnalysisService,
                           FileUploadService fileUploadService, SectionAnalysisService sectionAnalysisService) {
        this.analysisRepository = analysisRepository;
        this.summaryAnalysisService = summaryAnalysisService;
        this.fileUploadService = fileUploadService;
        this.sectionAnalysisService = sectionAnalysisService;
    }

    @Transactional
    public String analyse(Integer analysisId, AnalysisSection analysisSection) {
        Analysis analysis = analysisRepository.findByIdWithLock(analysisId).orElseThrow(DataNotFoundException::new);

        // For single document analysis
        if (Objects.nonNull(analysis.getUploadedDocument())) {
            analyseUploadedDocument(analysis, Boolean.FALSE);
            return analysis.getSummary();
        }

        // For project analysis
        return analyzeProject(analysis, analysisSection, Boolean.FALSE);
    }

    @SuppressWarnings("UnusedReturnValue")
    @Transactional
    public String analyzeProject(Analysis analysis, AnalysisSection analysisSection, Boolean reAnalyze) {
        Project project = analysis.getProject();

        if (Objects.nonNull(analysis.getUploadedDocument())) {
            throw new IllegalArgumentException("Analysis(%d) not targeted for project".formatted(analysis.getId()));
        }

        if (Objects.isNull(analysisSection)) {
            throw new IllegalArgumentException("AnalysisSection not provided for project Analysis(%d)".formatted(analysis.getId()));
        }

        if (ObjectUtils.isEmpty(project.getUploadedDocuments())) {
            logger.info("No uploaded documents found for Project({}) and Analysis({})", project.getId(), analysis.getId());
            return "No Uploaded document found for Project: %s".formatted(project.getName());
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

        String documentAnalysisSummary = analysisList.stream()
                                                     .map(Analysis::getSummary)
                                                     .collect(Collectors.joining("\n\n"));

//        if (isProjectUpdated || Boolean.TRUE.equals(reAnalyze) || !Boolean.TRUE.equals(analysis.getIsAnalyzed())) {
//
//            String summary;
//            summary = summaryAnalysisService.summaryAnalysis(documentAnalysisSummary, null);
//            analysis.setSummary(summary);
//            analysis.setIsAnalyzed(Boolean.TRUE);
//            analysis.setAnalyzedAt(LocalDateTime.now());
//
//            analysis.getAnalysisDetailList().clear();
//
//            // todo:: generate set based on document type
//            Set<AnalysisSection> sections = EnumSet.of(AnalysisSection.GENERAL_DETAILS);
//
//            for (AnalysisSection section : sections) {
//                AnalysisDetail analysisDetail = new AnalysisDetail();
//                analysisDetail.setAnalysis(analysis);
//
//                String analysisContent = sectionAnalysisService.sectionAnalysis(documentAnalysisSummary, section);
//                analysisDetail.setAnalysisContent(analysisContent);
//
//                analysis.getAnalysisDetailList().add(analysisDetail);
//            }
//
//        }

        AnalysisDetail analysisDetail;
        analysisDetail = analysis.getAnalysisDetailList()
                                 .stream()
                                 .filter(detail -> analysisSection.equals(detail.getAnalysisSection()))
                                 .findFirst()
                                 .orElse(null);

        if (analysisDetail == null) {
            analysisDetail = new AnalysisDetail();
            analysisDetail.setAnalysis(analysis);
            analysisDetail.setAnalysisSection(analysisSection);
            analysis.getAnalysisDetailList().add(analysisDetail);

        }

        if (isProjectUpdated || Boolean.TRUE.equals(reAnalyze) || !Boolean.TRUE.equals(analysisDetail.getIsAnalyzed())) {

            String analysisContent = sectionAnalysisService.sectionAnalysis(documentAnalysisSummary, analysisSection);
            analysisDetail.setAnalysisContent(analysisContent);

            analysisDetail.setIsAnalyzed(Boolean.TRUE);
            analysisDetail.setAnalyzedAt(LocalDateTime.now());
        }

        analysisRepository.save(analysis);

        return analysisDetail.getAnalysisContent();
    }

    @Transactional
    public boolean analyseUploadedDocument(Analysis analysis, Boolean reAnalyze) {
        UploadedDocument uploadedDocument = analysis.getUploadedDocument();

        if (Objects.isNull(uploadedDocument)) {
            throw new IllegalArgumentException("Analysis(%d) not targeted for UploadedDocument".formatted(analysis.getId()));
        }

        logger.info("Analyzing UploadedDocument({}) and Analysis({})", uploadedDocument.getId(), analysis.getId());

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
