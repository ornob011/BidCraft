package com.dsi.hackathon.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class VectorFileService {
    private static final Logger logger = LoggerFactory.getLogger(VectorFileService.class);
    private final PgVectorStore vectorStore;

    public VectorFileService(PgVectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void save(MultipartFile file, Map<String, Object> metaData) {
        Resource resource = file.getResource();
        DocumentReader documentReader = getPdfDocumentReader(resource);

        List<Document> documentList = documentReader.read();

        if (!ObjectUtils.isEmpty(metaData)) {
            documentList.forEach(document -> document.getMetadata().putAll(metaData));
        }

        DocumentTransformer transformer = new TokenTextSplitter();
        vectorStore.accept(transformer.apply(documentList));
        logger.info("Saved vector file: {}", resource.getFilename());
    }

    private DocumentReader getTikaDocumentReader(Resource resource) {
        return new TikaDocumentReader(resource);
    }

    public DocumentReader getPdfDocumentReader(Resource resource) {
        PdfDocumentReaderConfig config;
        config = PdfDocumentReaderConfig.builder()
                                        .withPageExtractedTextFormatter(
                                            ExtractedTextFormatter.builder()
                                                                  .withNumberOfBottomTextLinesToDelete(0)
                                                                  .withNumberOfTopPagesToSkipBeforeDelete(0)
                                                                  .build()
                                        )
                                        .withPagesPerDocument(1)
                                        .build();

        return new PagePdfDocumentReader(resource, config);
    }
}
