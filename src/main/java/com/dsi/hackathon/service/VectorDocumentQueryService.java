package com.dsi.hackathon.service;

import com.dsi.hackathon.entity.UploadedDocument;
import com.dsi.hackathon.enums.MetaDataLabel;
import com.dsi.hackathon.util.VectorFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VectorDocumentQueryService {
    private static final Logger logger = LoggerFactory.getLogger(VectorDocumentQueryService.class);
    private final PgVectorStore vectorStore;

    public VectorDocumentQueryService(PgVectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    private Filter.Expression getFilterExpression(UploadedDocument uploadedDocument) {
        return VectorFilter.andEq(Map.of(
            MetaDataLabel.UPLOADED_DOC_ID, uploadedDocument.getId(),
            MetaDataLabel.PROJECT_ID, uploadedDocument.getProject().getId()
        ));
    }

    public List<Document> getDocuments(UploadedDocument uploadedDocument) {

        return vectorStore.similaritySearch(
            SearchRequest.builder()
                         .filterExpression(getFilterExpression(uploadedDocument))
                         .build()
        );
    }
}
