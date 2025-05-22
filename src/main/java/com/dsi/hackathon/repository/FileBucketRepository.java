package com.dsi.hackathon.repository;

import com.dsi.hackathon.entity.FileBucket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileBucketRepository extends JpaRepository<FileBucket, Integer> {
}
