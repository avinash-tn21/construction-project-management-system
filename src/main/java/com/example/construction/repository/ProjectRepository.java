package com.example.construction.repository;

import com.example.construction.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Integer> {

    List<Project> findByClientId(Integer clientId);

    List<Project> findByContractorId(Integer contractorId);
}