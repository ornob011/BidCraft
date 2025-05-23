package com.dsi.hackathon.service;

import com.dsi.hackathon.entity.UploadedDocument;
import com.dsi.hackathon.enums.MetaDataLabel;
import com.dsi.hackathon.enums.UploadedDocumentType;
import com.dsi.hackathon.prompts.AnalysisPrompts;
import com.dsi.hackathon.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;

@Service
public class AnalysisService {
    private static final Logger logger = LoggerFactory.getLogger(AnalysisService.class);

    private final VectorFileService vectorFileService;
    private final PgVectorStore vectorStore;
    private final ChatClient chatClient;
    private final AnalysisPrompts analysisPrompts;

    public AnalysisService(ChatClient.Builder builder, PgVectorStore vectorStore, VectorFileService vectorFileService, AnalysisPrompts analysisPrompts) {
        this.chatClient = builder.defaultSystem(analysisPrompts.getAnalyzerSystemMsg()).build();
        this.vectorStore = vectorStore;
        this.vectorFileService = vectorFileService;
        this.analysisPrompts = analysisPrompts;
    }

    public String summeryAnalysis(UploadedDocument uploadedDocument) {
        logger.info("Generating Summary for UploadedDocument({})", uploadedDocument.getId());

        SearchRequest searchRequest;
        searchRequest = SearchRequest.builder()
                                     .filterExpression(MetaDataLabel.UPLOADED_DOC_ID.eq(uploadedDocument.getId()))
                                     .build();

        // fetch documents form vector store for uploaded document
        String documentStr = vectorStore.similaritySearch(searchRequest).stream()
                                        .map(Document::getText)
                                        .collect(Collectors.joining("\n"));

        return summeryAnalysis(documentStr, uploadedDocument.getUploadedDocumentType());
    }

    public String summeryAnalysis(Resource file, UploadedDocumentType documentType) {
        logger.info("Generating Summary for file: {}", file.getFilename());

        String documentStr;
        documentStr = vectorFileService.getPdfDocumentReader(file)
                                       .get()
                                       .stream()
                                       .map(Document::getText)
                                       .collect(Collectors.joining("\n"));

        return summeryAnalysis(documentStr, documentType);
    }

    public String summeryAnalysis(String content, UploadedDocumentType documentType) {
        logger.info("Generating Summary for string content");

        Resource userMsgResource;
        userMsgResource = analysisPrompts.getAnalysisTemplate(documentType);

        // call api with specified prompts
        String summary;
        summary = chatClient.prompt()
                            .user(promptUserSpec -> promptUserSpec.text(userMsgResource)
                                                                  .param("document", content))
                            .call()
                            .content();

        return summary;
    }

    public String summeryAnalysis(MultipartFile file, UploadedDocumentType documentType) {
        return summeryAnalysis(file.getResource(), documentType);
    }

}
