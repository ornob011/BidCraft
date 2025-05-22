package com.dsi.hackathon.repository;

import com.dsi.hackathon.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
}
