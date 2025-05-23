package com.dsi.hackathon.service;

import com.dsi.hackathon.entity.UploadedDocument;
import com.dsi.hackathon.enums.MetaDataLabel;
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

    @Value("classpath:/prompts/system/analyzer.st")
    private Resource analyzerSystemMsg;

    @Value("classpath:/prompts/tor-summarizer.st")
    private Resource torSummarizerTemplate;

    private final PgVectorStore vectorStore;
    private final ChatClient chatClient;

    public AnalysisService(ChatClient.Builder builder, PgVectorStore vectorStore, VectorFileService vectorFileService) {
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
        this.vectorFileService = vectorFileService;
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
                                        .map(TextUtils::cleanUpText)
                                        .collect(Collectors.joining("\n"));

        // call api with specified prompts
        String summary;
        summary = chatClient.prompt()
                            .system(analyzerSystemMsg)
                            .user(promptUserSpec ->
                                promptUserSpec.text(torSummarizerTemplate).param("document", documentStr)
                            )
                            .call()
                            .content();

        return summary;
    }

    public String summeryAnalysis(MultipartFile file) {
        logger.info("Generating Summary for file: {}", file.getOriginalFilename());

        String documentStr;
        documentStr = vectorFileService.getPdfDocumentReader(file.getResource())
                                       .get()
                                       .stream()
                                       .map(Document::getText)
                                       .map(TextUtils::cleanUpText)
                                       .collect(Collectors.joining("\n"));

        // call api with specified prompts
        String summary;
        summary = chatClient.prompt()
                            .system(analyzerSystemMsg)
                            .user(promptUserSpec ->
                                promptUserSpec.text(torSummarizerTemplate).param("document", documentStr)
                            )
                            .call()
                            .content();

        return summary;
    }
}
