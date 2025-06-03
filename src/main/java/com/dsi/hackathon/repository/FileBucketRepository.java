package com.dsi.hackathon.repository;

import com.dsi.hackathon.entity.FileBucket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileBucketRepository extends JpaRepository<FileBucket, Integer> {
    Optional<FileBucket> findByName(String fileName);
}
