  project-management
package com.example.construction.repository;

import com.example.construction.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MilestoneRepository extends JpaRepository<Milestone, Integer> {

    List<Milestone> findByProjectId(Integer projectId);
=======
package com.example.construction.repository;

import com.example.construction.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MilestoneRepository extends JpaRepository<Milestone, Integer> {

    List<Milestone> findByProjectId(Integer projectId);
  main
}