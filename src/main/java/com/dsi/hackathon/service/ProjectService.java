package com.dsi.hackathon.service;

import com.dsi.hackathon.entity.FileBucket;
import com.dsi.hackathon.entity.Project;
import com.dsi.hackathon.entity.UploadedDocument;
import com.dsi.hackathon.enums.MetaDataLabel;
import com.dsi.hackathon.exception.DataNotFoundException;
import com.dsi.hackathon.repository.FileBucketRepository;
import com.dsi.hackathon.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {
    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);
    private final ProjectRepository projectRepository;
    private final MinioCleanupService minioCleanupService;
    private final FileBucketRepository fileBucketRepository;
    private final PgVectorStore vectorStore;

    public ProjectService(ProjectRepository projectRepository, MinioCleanupService minioCleanupService, FileBucketRepository fileBucketRepository, PgVectorStore vectorStore) {
        this.projectRepository = projectRepository;
        this.minioCleanupService = minioCleanupService;
        this.fileBucketRepository = fileBucketRepository;
        this.vectorStore = vectorStore;
    }

    @Transactional
    public void delete(Integer projectId) {
        logger.info("Deleting Project({})", projectId);

        Project project = projectRepository.findById(projectId)
                                           .orElseThrow(DataNotFoundException.supplier(Project.class, projectId));

        List<FileBucket> fileBucketList = project.getUploadedDocuments()
                                       .stream()
                                       .map(UploadedDocument::getFileBucket)
                                       .toList();

        // Delete physical files
        for (FileBucket fileBucket : fileBucketList) {
            minioCleanupService.deleteFileAsync(
                fileBucket.getName()
            );
        }

        fileBucketRepository.deleteAll(fileBucketList);

        // Delete from vector space
        vectorStore.delete(MetaDataLabel.PROJECT_ID.eq(project.getId().toString()));

        projectRepository.delete(project);
        logger.info("Deleted Project({}) Successfully", projectId);
    }
}
