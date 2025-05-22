package com.dsi.hackathon.repository;

import com.dsi.hackathon.entity.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisRepository extends JpaRepository<Analysis, Integer> {
}
